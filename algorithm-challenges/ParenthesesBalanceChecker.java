package sprint;

public class ParenthesesBalanceChecker {
    public boolean isBalanced(String str) {
        if (str==null) {
            return false;
        }
        return checkBalance(str, 0, 0);
    }

    private boolean checkBalance(String str, int index, int bal) {
        if (bal<0) {
            return false;
        } else if (index>str.length()-1) {
            return bal==0;
        } else if (str.charAt(index)=='(') {
            return checkBalance(str, index+1, bal+1);
        } else if (str.charAt(index)==')') {
            return checkBalance(str, index+1, bal-1);
        } else {
            return checkBalance(str, index+1, bal);
        }
    }
}