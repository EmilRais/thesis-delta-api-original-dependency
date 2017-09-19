package dk.developer.clause;

public interface Create<Input, Output> {
    Output create(Input input);

    interface Void<Input> {
        void create(Input input);
    }
}
