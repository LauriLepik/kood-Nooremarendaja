public class InputData {
    private int operation;
    private int cypher;
    private String message;

    public InputData(int operation, int cypher, String message) {
        this.operation = operation;
        this.cypher = cypher;
        this.message = message;
    }

    public int getOperation() {
        return operation;
    }

    public int getCypher() {
        return cypher;
    }

    public String getMessage() {
        return message;
    }
}
