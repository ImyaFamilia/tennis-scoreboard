package imya.tennis.service.score;

public enum Score {
    ZERO("0"), FIFTEEN("15"), THIRTY("30"), FORTY("40"), GAME("AD");

    private final String name;

    Score(String name) {
        this.name = name;
    }

    public Score next() {
        if (this == GAME) {
            throw new IllegalStateException("Cannot call next() on GAME");
        } else {
            return Score.values()[this.ordinal() + 1];
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
