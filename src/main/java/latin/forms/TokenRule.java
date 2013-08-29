
package latin.forms;

import com.google.common.base.Function;

public interface TokenRule extends Function<Token,Token> {
    String getSpec();
}