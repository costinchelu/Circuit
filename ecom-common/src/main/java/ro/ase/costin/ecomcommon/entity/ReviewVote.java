package ro.ase.costin.ecomcommon.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "review_votes")
@Getter
@Setter
public class ReviewVote extends IdBasedEntity {

    private int votes;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

    private static final int VOTE_UP_POINT = 1;

    private static final int VOTE_DOWN_POINT = -1;

    public void voteUp() {
        this.votes = VOTE_UP_POINT;
    }

    public void voteDown() {
        this.votes = VOTE_DOWN_POINT;
    }

    @Transient
    public boolean isUpvoted() {
        return this.votes == VOTE_UP_POINT;
    }

    @Transient
    public boolean isDownvoted() {
        return this.votes == VOTE_DOWN_POINT;
    }

    @Override
    public String toString() {
        return "Vot [voturi=" + votes + ", client=" + customer.getFullName() + ", recenzie=" + review.getId() + "]";
    }
}
