package macrame

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.StaticAnnotation

class delegate extends StaticAnnotation {
   def macroTransform(annottees : Any*) = macro delegate.impl
}

object delegate {

   def impl(c : Context)(annottees : c.Expr[Any]*) : c.Expr[Any] = {
      import c.universe._
      import Flag._

      val (delegate : Tree, container : Option[Tree], companion : Option[Tree]) = annottees match {
         case delegate :: clazz :: obj :: Nil ⇒ (delegate.tree, Some(clazz.tree), Some(obj.tree))
         case delegate :: clazz :: Nil        ⇒ (delegate.tree, Some(clazz.tree), None)
         case delegate :: Nil                 ⇒ (delegate.tree, None, None)
      }
      container match {
         case Some(tree) ⇒ parameterImpl(c)(delegate, tree, companion)
         case None       ⇒ methodImpl(c)(delegate)
      }
   }

   def methodImpl(c : Context)(delegate : c.Tree) : c.Expr[Any] = {

      import c.universe._
      import Flag._

      def getType(tpt : Tree) : Type =
         c.typecheck(q"""(throw new java.lang.Exception("")) : $tpt""").tpe

      def owners(sym : Symbol) : List[Symbol] = {
         var curr = sym.owner
         var result = List.empty[Symbol]
         while (curr != NoSymbol && curr.name.toString != "<root>") {
            result = curr :: result
            curr = curr.owner
         }
         return result
      }

      def paramTypeName(param : Symbol, enclosingTypes : List[Symbol]) : Tree = {
         val typeSymbol = param.typeSignature.typeSymbol
         val typeVars = enclosingTypes.map(_.name.toTypeName)
         val (fqpn, _) = (owners(typeSymbol) :+ typeSymbol).filter(_.isType) match {
            case h :: t ⇒ t.foldLeft[(Tree, Boolean)]((Ident(h.name.toTermName), false)) {
               case ((res, isType), curr) ⇒
                  val currIsType = curr.isType && !curr.isPackage
                  if (isType)
                     (SelectFromTypeTree(res, curr.name.toTypeName), true)
                  else
                     (
                        Select(
                           res,
                           if (currIsType)
                              curr.name.toTypeName
                           else
                              curr.name.toTermName),
                           isType || currIsType)
            }
            case _ ⇒ (EmptyTree, false)
         }
         if (fqpn != EmptyTree && !typeVars.contains(typeSymbol.name.toTypeName))
            fqpn
         else
            Ident(typeSymbol.name.toTypeName)
      }

      val (delegateName, delegateType) = delegate match {
         case ValDef(_, name, tpt, _) ⇒
            name -> getType(tpt)
         case DefDef(_, name, tparams, vparamss, tpt, _) if tparams.isEmpty && vparamss.isEmpty ⇒
            name -> getType(tpt)
         case _ ⇒
            c.abort(NoPosition, "Delegate must be a parameter or method with no arguments.")
      }

      val containerType = c.typeOf[AnyRef]

      val delegatedMembers : List[Tree] = delegateType.members.filter(s ⇒
         (containerType.member(s.name) == NoSymbol) &&
            (s.name.decodedName.toString != "<init>") &&
            !(delegateType.typeSymbol.asClass.isCaseClass && s.name.decodedName.toString == "copy") &&
            !s.isPrivate &&
            !s.isProtected
      ).collect {
         case method : MethodSymbol ⇒
            val arguments = method.paramLists.map(_.map { p ⇒
               ValDef(
                  Modifiers(PARAM),
                  p.name.toTermName,
                  paramTypeName(p, method.typeParams),
                  EmptyTree)
            })
            val methodName = method.name.toTermName
            val typeArgs = method.typeParams.map(t ⇒
               TypeDef(
                  Modifiers(PARAM),
                  t.name.toTypeName,
                  List(),
                  TypeBoundsTree(tq"${c.typeOf[Nothing]}", tq"${c.typeOf[Any]}")))
            val passedTypeArgs = method.typeParams.map(t ⇒ Ident(t.name.toTypeName))

            val methodCall =
               if (passedTypeArgs.nonEmpty)
                  TypeApply(
                     Select(
                        Ident(delegateName),
                        methodName),
                     passedTypeArgs)
               else
                  Select(
                     Ident(delegateName),
                     methodName)

            val rhs = arguments.foldLeft[Tree](methodCall) {
               case (tree, args) ⇒ Apply(tree, args.map(v ⇒ Ident(v.name)))
            }
            val mods =
               if (method.isImplicit)
                  Modifiers(IMPLICIT)
               else
                  Modifiers()
            DefDef(
               mods,
               methodName,
               typeArgs,
               arguments,
               TypeTree(),
               rhs)
      }.toList

      // delegatedMembers.foreach(println)
      c.Expr[Any](Block(delegate :: delegatedMembers, Literal(Constant(()))))
   }

   def parameterImpl(c : Context)(
      delegate : c.Tree,
      container : c.Tree,
      companion : Option[c.Tree]) : c.Expr[Any] = {

      import c.universe._
      import Flag._

      def getType(tpt : Tree) : Type =
         c.typecheck(q"""(throw new java.lang.Exception("")) : $tpt""").tpe

      val (delegateName, delegateType) = delegate match {
         case ValDef(_, name, tpt, _) ⇒
            name -> getType(tpt)
         case _ ⇒
            c.abort(NoPosition, "Delegate must be a parameter or method.")
      }
      val output = container match {
         case ClassDef(mods, containerName, tparams, impl) ⇒
            val existingMembers = impl.body.flatMap {
               case DefDef(_, name, _, _, _, _) ⇒ List(name)
               case ValDef(_, name, _, _)       ⇒ List(name)
               case t                           ⇒ Nil
            }
            val containerType = c.typecheck(q"""(throw new java.lang.Exception("")) : Object with ..${impl.parents}""").tpe
            val delegatedMembers = delegateType.members.filter(s ⇒
               !existingMembers.contains(s.name) &&
                  (containerType.member(s.name) == NoSymbol) &&
                  (s.name.decodedName.toString != "<init>") &&
                  !(delegateType.typeSymbol.asClass.isCaseClass && s.name.decodedName.toString == "copy") &&
                  !s.isPrivate &&
                  !s.isProtected
            ).collect {
               case method : MethodSymbol ⇒
                  val arguments = method.paramLists.map(_.map { p ⇒
                     val typeSignature = p.typeSignature match {
                        case TypeRef(NoPrefix, t, Nil) ⇒
                           Ident(p.typeSignature.typeSymbol.name.toTypeName)
                        case _ ⇒ tq"${p.typeSignature}"
                     }
                     ValDef(
                        Modifiers(PARAM),
                        p.name.toTermName,
                        typeSignature,
                        EmptyTree)
                  })
                  val methodName = method.name.toTermName
                  val typeArgs = method.typeParams.map(t ⇒
                     TypeDef(
                        Modifiers(PARAM),
                        t.name.toTypeName,
                        List(),
                        TypeBoundsTree(tq"${c.typeOf[Nothing]}", tq"${c.typeOf[Any]}")))
                  val passedTypeArgs = method.typeParams.map(t ⇒ Ident(t.name.toTypeName))

                  val methodCall =
                     if (passedTypeArgs.nonEmpty)
                        TypeApply(
                           Select(
                              Ident(delegateName),
                              methodName),
                           passedTypeArgs)
                     else
                        Select(
                           Ident(delegateName),
                           methodName)

                  val rhs = arguments.foldLeft[Tree](methodCall) {
                     case (tree, args) ⇒ Apply(tree, args.map(v ⇒ Ident(v.name)))
                  }
                  DefDef(
                     Modifiers(),
                     methodName,
                     typeArgs,
                     arguments,
                     TypeTree(),
                     rhs)
            }

            ClassDef(mods, containerName, tparams, Template(impl.parents, impl.self, impl.body ++ delegatedMembers))
         case _ ⇒ c.abort(NoPosition, "Delegate must be a parameter or method of a class.")
      }
      // println(output)
      c.Expr[Any](Block(output :: companion.toList, Literal(Constant(()))))
   }
}
