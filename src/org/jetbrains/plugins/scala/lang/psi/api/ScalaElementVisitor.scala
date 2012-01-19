package org.jetbrains.plugins.scala.lang.psi.api

import base.types._
import base.{ScModifierList, ScConstructor, ScLiteral, ScReferenceElement}
import org.jetbrains.plugins.scala.lang.psi.ScalaPsiElement
import com.intellij.psi.{PsiFile, PsiElementVisitor}
import org.jetbrains.plugins.scala.lang.psi.api.expr._
import org.jetbrains.plugins.scala.lang.psi.api.base.patterns.{ScCaseClause, ScPattern}
import org.jetbrains.plugins.scala.lang.psi.api.toplevel.typedef.ScTypeDefinition
import statements.params.{ScParameters, ScParameter}
import statements.{ScFunctionDefinition, ScFunction, ScVariableDefinition, ScPatternDefinition}
import toplevel.imports.ScImportExpr
import org.jetbrains.plugins.scala.lang.scaladoc.psi.api._
import collection.mutable.Stack
import org.jetbrains.plugins.scala.lang.psi.impl.statements.params.ScParametersImpl

/**
 * @author ilyas
 * @author Alexander Podkhalyuzin
 */

class ScalaRecursiveElementVisitor extends ScalaElementVisitor {
  private val referencesStack = new Stack[ScReferenceElement]()
  
  override def visitElement(element: ScalaPsiElement) {
    if (!referencesStack.isEmpty && referencesStack.top == element) {
      referencesStack.pop()
      referencesStack.push(null)
    } else {
      element.acceptChildren(this)
    }
  }

  override def visitReferenceExpression(ref: ScReferenceExpression) {
    try {
      referencesStack.push(ref)
      visitReference(ref)
      visitExpression(ref)
    } finally {
      referencesStack.pop()
    }
  }

  override def visitTypeProjection(proj: ScTypeProjection) {
    try {
      referencesStack.push(proj)
      visitReference(proj)
      visitTypeProjection(proj)
    } finally {
      referencesStack.pop()
    }
  }
}

class ScalaElementVisitor extends PsiElementVisitor {
  def visitParameters(parameters: ScParameters) {visitElement(parameters)}

  def visitModifierList(modifierList: ScModifierList) {visitElement(modifierList)}

  def visitConstructor(constr: ScConstructor) {visitElement(constr)}

  def visitFunctionDefinition(fun: ScFunctionDefinition) {visitFunction(fun)}

  def visitCatchBlock(c: ScCatchBlock) {visitElement(c)}

  override def visitFile(file: PsiFile) {
    file match {
      case sf: ScalaFile => visitElement(sf)
      case _ => visitElement(file)
    }
  }

  def visitElement(element: ScalaPsiElement) {super.visitElement(element)}

  //Override also visitReferenceExpression! and visitTypeProjection!
  def visitReference(ref: ScReferenceElement) { visitElement(ref) }
  def visitParameter(parameter: ScParameter) {visitElement(parameter)}
  def visitPatternDefinition(pat: ScPatternDefinition) { visitElement(pat) }
  def visitVariableDefinition(varr: ScVariableDefinition) { visitElement(varr) }
  def visitCaseClause(cc: ScCaseClause) { visitElement(cc) }
  def visitPattern(pat: ScPattern) { visitElement(pat) }
  def visitEnumerator(enum: ScEnumerator) { visitElement(enum) }
  def visitGenerator(gen: ScGenerator) { visitElement(gen) }
  def visitGuard(guard: ScGuard) { visitElement(guard) }
  def visitFunction(fun: ScFunction) { visitElement(fun) }
  def visitTypeDefintion(typedef: ScTypeDefinition) { visitElement(typedef) }
  def visitImportExpr(expr: ScImportExpr) {visitElement(expr)}
  def visitSelfInvocation(self: ScSelfInvocation) {visitElement(self)}
  def visitAnnotation(annotation: ScAnnotation) {visitElement(annotation)}


  // Expressions
  //Override also visitReferenceExpression!
  def visitExpression(expr: ScExpression) { visitElement(expr) }
  def visitThisReference(t: ScThisReference) {visitExpression(t)}
  def visitSuperReference(t: ScSuperReference) {visitExpression(t)}
  def visitReferenceExpression(ref: ScReferenceExpression) {}
  def visitPostfixExpression(p: ScPostfixExpr) { visitExpression(p) }
  def visitPrefixExpression(p: ScPrefixExpr) { visitExpression(p) }
  def visitIfStatement(stmt: ScIfStmt) { visitExpression(stmt) }
  def visitLiteral(l: ScLiteral) {visitExpression(l)}
  def visitAssignmentStatement(stmt: ScAssignStmt) { visitExpression(stmt) }
  def visitMethodCallExpression(call: ScMethodCall) { visitExpression(call) }
  def visitGenericCallExpression(call: ScGenericCall) { visitExpression(call) }
  def visitInfixExpression(infix: ScInfixExpr) {visitExpression(infix)}
  def visitWhileStatement(ws: ScWhileStmt) { visitExpression(ws) }
  def visitReturnStatement(ret: ScReturnStmt) { visitExpression(ret) }
  def visitMatchStatement(ms: ScMatchStmt) { visitExpression(ms) }
  def visitForExpression(expr: ScForStatement) { visitExpression(expr) }
  def visitDoStatement(stmt: ScDoStmt) { visitExpression(stmt) }
  def visitFunctionExpression(stmt: ScFunctionExpr) { visitExpression(stmt) }
  def visitThrowExpression(throwStmt: ScThrowStmt) { visitExpression(throwStmt) }
  def visitTryExpression(tryStmt: ScTryStmt) { visitExpression(tryStmt) }
  def visitExprInParent(expr: ScParenthesisedExpr) {visitExpression(expr)}
  def visitNewTemplateDefinition(templ: ScNewTemplateDefinition) {visitExpression(templ)}
  def visitTypedStmt(stmt: ScTypedStmt) {visitExpression(stmt)}
  def visitTupleExpr(tuple: ScTuple) {visitExpression(tuple)}
  def visitBlockExpression(block: ScBlockExpr) {visitExpression(block)}
  def visitConstrBlock(constr: ScConstrBlock) {visitBlockExpression(constr)}

  //type elements
  //Override also visitTypeProjection!
  def visitTypeElement(te: ScTypeElement) {visitElement(te)}
  def visitSimpleTypeElement(simple: ScSimpleTypeElement) {visitTypeElement(simple)}
  def visitWildcardTypeElement(wildcard: ScWildcardTypeElement) {visitTypeElement(wildcard)}
  def visitTypeProjection(proj: ScTypeProjection) {}
  def visitTupleTypeElement(tuple: ScTupleTypeElement) {visitTypeElement(tuple)}
  def visitParenthesisedTypeElement(parenthesised: ScParenthesisedTypeElement) {visitTypeElement(parenthesised)}
  def visitParameterizedTypeElement(parameterized: ScParameterizedTypeElement) {visitTypeElement(parameterized)}
  def visitInfixTypeElement(infix: ScInfixTypeElement) {visitTypeElement(infix)}
  def visitFunctionalTypeElement(fun: ScFunctionalTypeElement) {visitTypeElement(fun)}
  def visitExistentialTypeElement(exist: ScExistentialTypeElement) {visitTypeElement(exist)}
  def visitCompoundTypeElement(compound: ScCompoundTypeElement) {visitTypeElement(compound)}
  def visitAnnotTypeElement(annot: ScAnnotTypeElement) {visitTypeElement(annot)}

  //scaladoc
  def visitDocComment(s: ScDocComment) {visitComment(s)}
  def visitScaladocElement(s: ScalaPsiElement) {visitElement(s)}
  def visitWikiSyntax(s: ScDocSyntaxElement) {visitElement(s)}
  def visitInlinedTag(s: ScDocInlinedTag) {visitElement(s)}
  def visitTag(s: ScDocTag) {visitElement(s)}
}