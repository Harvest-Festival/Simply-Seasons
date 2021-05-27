package uk.joshiejack.simplyseasons.api;

public enum Season {
    SPRING, SUMMER, AUTUMN, WINTER, WET, DRY;

    public static final Season[] MAIN = new Season[4];
    static {
        System.arraycopy(values(), 0, MAIN, 0, 4);
    }
}