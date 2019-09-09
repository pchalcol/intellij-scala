package org.jetbrains.plugins.scala.lang.lexer;

import junit.framework.Test;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.scala.PerfCycleTests;
import org.jetbrains.plugins.scala.util.TestUtils;
import org.junit.experimental.categories.Category;

@Category({PerfCycleTests.class})
public class FailedLexerTest extends LexerTestBase {

    protected FailedLexerTest() {
        super(TestUtils.getTestDataPath() + "/lexer/failed");
    }

    @Override
    protected void printTokenRange(int tokenStart, int tokenEnd,
                                   @NotNull StringBuilder builder) {
    }

    @Override
    protected boolean shouldPass() {
        return false;
    }

    @NotNull
    public static Test suite() {
        return new FailedLexerTest();
    }
}