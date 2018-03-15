package uk.q3c.krail.core.view;

/**
 * Created by David Sowerby on 14 Mar 2018
 */
class Widget<T> {
    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
