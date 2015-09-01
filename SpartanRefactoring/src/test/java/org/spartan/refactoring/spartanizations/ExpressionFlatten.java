package org.spartan.refactoring.spartanizations;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.spartan.hamcrest.CoreMatchers.is;
import static org.spartan.hamcrest.MatcherAssert.assertThat;
import static org.spartan.refactoring.utils.Funcs.left;
import static org.spartan.refactoring.utils.Into.e;
import static org.spartan.refactoring.utils.Into.i;
import static org.spartan.refactoring.utils.Restructure.flatten;

import org.eclipse.jdt.core.dom.InfixExpression;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.spartan.refactoring.utils.Is;

@SuppressWarnings({ "javadoc", "static-method" }) //
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
public class ExpressionFlatten {
  @Test public void flattenExists() {
    final InfixExpression e = i("1+2");
    flatten(e);
  }
  @Test public void flattenIsDistinct() {
    final InfixExpression e = i("1+2");
    final InfixExpression $ = flatten(e);
    assertThat($, is(not(e)));
  }
  @Test public void flattenIsNotNull() {
    final InfixExpression e = i("1+2");
    final InfixExpression $ = flatten(e);
    assertThat($, is(not(nullValue())));
  }
  @Test public void flattenIsSame() {
    final InfixExpression e = i("1+2");
    final InfixExpression $ = flatten(e);
    assertThat($.toString(), is(e.toString()));
  }
  @Test public void flattenLeftArgument() {
    assertThat(left(flatten(i("1+2"))).toString(), is("1"));
  }
  @Test public void flattenOfDeepParenthesisIsCorrect() {
    final InfixExpression e = i("(((1+2)))+(((3 + (4+5))))");
    assertThat(flatten(e).toString(), is("1 + 2 + 3+ 4+ 5"));
  }
  @Test public void flattenOfDeepParenthesisSize() {
    final InfixExpression e = i("(1+(2))+(3)");
    assertThat(flatten(e).extendedOperands().size(), is(1));
  }
  @Test public void flattenOfDeepParenthesOtherOperatorsisIsCorrect() {
    final InfixExpression e = i("(((1+2)))+(((3 + (4*5))))");
    assertThat(flatten(e).toString(), is("1 + 2 + 3+ 4 * 5"));
  }
  @Test public void flattenOfParenthesis() {
    final InfixExpression e = i("1+2+(3)");
    assertThat(flatten(e).extendedOperands().size(), is(1));
  }
  @Test public void flattenOfTrivialDoesNotAddOperands() {
    final InfixExpression e = i("1+2");
    assertThat(e.extendedOperands().size(), is(0));
  }
  @Test public void hasExtendedOperands() {
    final InfixExpression e = i("1+2");
    assertThat(e.hasExtendedOperands(), is(false));
  }
  @Test public void leftOperandIsNotString() {
    final InfixExpression e = i("1+2");
    assertTrue(Is.notString(left(e)));
  }
  @Test public void isNotStringInfixFalse() {
    final InfixExpression e = i("1+f");
    assertFalse(Is.notString(e));
  }
  @Test public void isNotStringInfixPlain() {
    assertFalse(Is.notString(e("1+f")));
  }
  @Test public void leftOperandIsNumeric() {
    final InfixExpression e = i("1+2");
    assertTrue(Is.numericLiteral(left(e)));
  }
  @Test public void leftOperandIsOne() {
    final InfixExpression e = i("1+2");
    assertThat(left(e).toString(), is("1"));
  }
  @Test public void leftOperandNotNull() {
    final InfixExpression e = i("1+2");
    assertThat(left(e), not(nullValue()));
  }
  @Test public void rightOperandNotNull() {
    final InfixExpression e = i("1+2");
    assertThat(left(e), not(nullValue()));
  }
}