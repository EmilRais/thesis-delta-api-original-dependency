package dk.developer.clause;

public interface Of<Input, Output> {
    Output of(Input input);

    interface Void<Input> {
        void of(Input input);
    }
}
