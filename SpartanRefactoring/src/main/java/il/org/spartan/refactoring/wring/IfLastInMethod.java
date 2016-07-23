package il.org.spartan.refactoring.wring;

import il.org.spartan.refactoring.preferences.*;
import il.org.spartan.refactoring.preferences.PluginPreferencesResources.WringGroup;
import il.org.spartan.refactoring.utils.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import static il.org.spartan.Utils.*;
import static il.org.spartan.refactoring.utils.Funcs.*;

import static il.org.spartan.refactoring.utils.extract.*;

/**
 * A {@link Wring} to convert <code><b>if</b> (a) { f(); g(); }</code> into
 * <code><b>if</b> (!a) return f(); g();</code> provided that this
 * <code><b>if</b></code> statement is the last statement in a method.
 *
 * @author Yossi Gil
 * @since 2015-09-09
 */
public class IfLastInMethod extends Wring<IfStatement> implements Kind.Simplify {
  @Override String description(final IfStatement s) {
    return "Invert conditional " + s.getExpression() + " for early return";
  }
  @Override Suggestion make(final IfStatement s) {
    if (Is.vacuousThen(s) || !Is.vacuousElse(s) || extract.statements(then(s)).size() < 2)
      return null;
    final Block b = asBlock(s.getParent());
    return b == null || !lastIn(s, b.statements()) || !(b.getParent() instanceof MethodDeclaration) ? null : new Suggestion(description(s), s) {
      @Override public void go(final ASTRewrite r, final TextEditGroup g) {
        Wrings.insertAfter(s, extract.statements(then(s)), r, g);
        final IfStatement newIf = duplicate(s);
        newIf.setExpression(duplicate(logicalNot(s.getExpression())));
        newIf.setThenStatement(s.getAST().newReturnStatement());
        newIf.setElseStatement(null);
        r.replace(s, newIf, g);
      }
    };
  }
  @Override public WringGroup kind() {
    // TODO Auto-generated method stub
    return null;
  }
  @Override public void go(final ASTRewrite r, final TextEditGroup g) {
    // TODO Auto-generated method stub
  }
}
