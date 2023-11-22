package ro.ase.costin.ecomcommon.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VoteResult {

    private boolean successful;

    private String message;

    private int voteCount;

    public static VoteResult fail(String message) {
        return new VoteResult(false, message, 0);
    }

    public static VoteResult success(String message, int voteCount) {
        return new VoteResult(true, message, voteCount);
    }
}
