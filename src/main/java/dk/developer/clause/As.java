package dk.developer.clause;

public interface As<Input, Output> {
    Output as(Input input);

    interface Void<Input> {
        void as(Input input);
    }
}
