package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.spartanizer.dispatch.Tippers.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.tipping.*;

/** convert
 *
 * <pre>
 * if (x) {
 *   ;
 *   f();
 *   return a;
 * } else {
 *   ;
 *   g();
 *   {
 *   }
 * }
 * </pre>
 *
 * into
 *
 * <pre>
 * if (x) {
 *   f();
 *   return a;
 * }
 * g();
 * </pre>
 *
 * @author Yossi Gil
 * @since 2015-07-29 */
public final class IfCommandsSequencerNoElseSingletonSequencer extends ReplaceToNextStatement<IfStatement> implements TipperCategory.EarlyReturn {
  @Override public String description(@SuppressWarnings("unused") final IfStatement __) {
    return "Invert conditional and use next statement)";
  }

  @Override protected ASTRewrite go(final ASTRewrite r, final IfStatement s, final Statement nextStatement, final TextEditGroup g) {
    if (!iz.vacuousElse(s) || !iz.sequencer(nextStatement) || !endsWithSequencer(then(s)))
      return null;
    final IfStatement asVirtualIf = subject.pair(then(s), nextStatement).toIf(s.getExpression());
    if (wizard.same(then(asVirtualIf), elze(asVirtualIf))) {
      r.replace(s, then(asVirtualIf), g);
      r.remove(nextStatement, g);
      return r;
    }
    if (!shoudlInvert(asVirtualIf))
      return null;
    final IfStatement canonicalIf = invert(asVirtualIf);
    final List<Statement> ss = extract.statements(elze(canonicalIf));
    canonicalIf.setElseStatement(null);
    if (!iz.block(s.getParent())) {
      ss.add(0, canonicalIf);
      r.replace(s, subject.ss(ss).toBlock(), g);
      r.remove(nextStatement, g);
    } else {
      final ListRewrite lr = insertAfter(s, ss, r, g);
      lr.replace(s, canonicalIf, g);
      lr.remove(nextStatement, g);
    }
    return r;
  }
}
