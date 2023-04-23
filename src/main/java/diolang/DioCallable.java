package diolang;

import java.util.List;

interface DioCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}