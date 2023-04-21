package diolang;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class DioTest {

    @Test
    public void testRun1() {
        String src = "INSTEAD OF RUNNING AWAY, YOU'RE COMING RIGHT TO ME?";
        Dio.run(src);

        Assert.assertEquals(TokenType.ELSE.name(), Dio.tempTokens.get(0).type.name());
    }

    @Test
    public void testRun2() {
        String src = "IF YOUD RATHER DIE THEN CLIMB THOSE STAIRS.";
        Dio.run(src);

        Assert.assertEquals(TokenType.WHILE.name(), Dio.tempTokens.get(0).type.name());
    }

    @Test
    public void testRun3() {
        String src = "I DON'T LET ANYONE SWAGGER OVER ME!";
        Dio.run(src);

        Assert.assertEquals(TokenType.AND.name(), Dio.tempTokens.get(0).type.name());
    }

    @Test
    public void testRun4() {
        String src = "INSTEAD OF RUNNING AWAY, YOU'RE COMING RIGHT TO ME?";
        Dio.run(src);

        Assert.assertEquals(src, Dio.tempTokens.get(0).lexeme);
    }
}