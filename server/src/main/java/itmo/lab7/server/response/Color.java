package itmo.lab7.server.response;

public enum Color {
    GREEN("\u001b[32m"), PURPLE("\u001b[35m"), RED("\u001B[31m"), RESET("\u001B[0m");

    private final String colorCode;

    Color(String colorCode) {
        this.colorCode = colorCode;
    }

    public String toString() {
        return colorCode;
    }
}
