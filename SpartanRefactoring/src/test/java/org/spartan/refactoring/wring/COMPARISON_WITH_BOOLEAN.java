package org.spartan.refactoring.wring;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.spartan.hamcrest.CoreMatchers.is;
import static org.spartan.hamcrest.MatcherAssert.assertThat;
import static org.spartan.hamcrest.OrderingComparison.greaterThanOrEqualTo;
import static org.spartan.refactoring.utils.Restructure.flatten;

import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.spartan.refactoring.spartanizations.Spartanization;
import org.spartan.refactoring.utils.All;
import org.spartan.utils.Utils;

/**
 * Unit tests for {@link Wrings#ADDITION_SORTER}.
 *
 * @author Yossi Gil
 * @since 2014-07-13
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
@SuppressWarnings({ "javadoc", }) //
public class COMPARISON_WITH_BOOLEAN extends AbstractWringTest {
  static final Wring WRING = Wrings.COMPARISON_WITH_BOOLEAN.inner;
  public COMPARISON_WITH_BOOLEAN() {
    super(WRING);
  }
  @Test public void comparisonWithSpecific0Legibiliy0() {
    assertNotWithinScope("this != a");
  }

  @RunWith(Parameterized.class) //
  public static class WringedInput extends AbstractWringTest.WringedExpression.Infix {
    static String[][] cases = Utils.asArray(//
        new String[] { "", "a == b == c == true", "a == b == c" }, //
        new String[] { "", "a == true", "a" }, //
        new String[] { "", "a == false", "!a" }, //
        new String[] { "", "true == a", "a" }, //
        new String[] { "", "a != true", "!a" }, //
        new String[] { "", "a != false", "a" }, //
        new String[] { "", "false == a", "!a" }, //
        new String[] { "", "true != a", "!a" }, //
        new String[] { "", "false != a", "a" }, //
        new String[] { "", "false != false", "false" }, //
        new String[] { "", "false != true", "true" }, //
        new String[] { "", "false == false", "true" }, //
        new String[] { "", "false == true", "false" }, //
        new String[] { "", "false != false", "false" }, //
        new String[] { "", "true != true", "false" }, //
        new String[] { "", "true != false", "true" }, //
        new String[] { "", "true != true", "false" }, //
        null);
    /**
     * Generate test cases for this parameterized class.
     *
     * @return a collection of cases, where each case is an array of three
     *         objects, the test case name, the input, and the file.
     */
    @Parameters(name = DESCRIPTION) //
    public static Collection<Object[]> cases() {
      return collect(cases);
    }
    static Document rewrite(final Spartanization s, final CompilationUnit u, final Document d) {
      try {
        s.createRewrite(u, null).rewriteAST(d, null).apply(d);
        return d;
      } catch (final MalformedTreeException e) {
        fail(e.getMessage());
      } catch (final IllegalArgumentException e) {
        fail(e.getMessage());
      } catch (final BadLocationException e) {
        fail(e.getMessage());
      }
      return null;
    }
    public WringedInput() {
      super(WRING);
    }
    @Override @Test public void flattenIsIdempotentt() {
      final InfixExpression flatten = flatten(asInfixExpression());
      assertThat(flatten(flatten).toString(), is(flatten.toString()));
    }
    @Override @Test public void inputIsInfixExpression() {
      assertNotNull(asInfixExpression());
    }
    @Test public void tryToSortTwice() {
      final List<Expression> operands = All.operands(flatten(asInfixExpression()));
      Wrings.tryToSort(operands, ExpressionComparator.ADDITION);
      assertFalse(Wrings.tryToSort(operands, ExpressionComparator.ADDITION));
    }
    @Test public void twoOrMoreArguments() {
      assertThat(All.operands(asInfixExpression()).size(), greaterThanOrEqualTo(2));
    }
  }
}
