package ro.ase.costin.ecomfront.review.vote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import ro.ase.costin.ecomcommon.data.VoteResult;
import ro.ase.costin.ecomcommon.entity.Review;
import ro.ase.costin.ecomfront.review.ReviewRepository;


@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Integration tests")
class ReviewVoteRestControllerTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testVoteNotLogin() throws Exception {
        String requestURL = "/vote_review/1/up";
        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("You must login");
    }

    @Test
    @WithMockUser(username = "user1", password = "pass1")
    void testVoteNonExistReview() throws Exception {
        String requestURL = "/vote_review/123/up";
        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertFalse(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("no longer exists");
    }

    @Test
    @WithMockUser(username = "user1", password = "pass1")
    void testVoteUp() throws Exception {
        Integer reviewId = 20;
        String requestURL = "/vote_review/" + reviewId + "/up";

        Review review = reviewRepository.findById(reviewId).get();
        int voteCountBefore = review.getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("successfully voted up");

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore + 1, voteCountAfter);

    }

    @Test
    @WithMockUser(username = "user1", password = "pass1")
    void testUndoVoteUp() throws Exception {
        Integer reviewId = 20;
        String requestURL = "/vote_review/" + reviewId + "/up";

        Review review = reviewRepository.findById(reviewId).get();
        int voteCountBefore = review.getVotes();

        MvcResult mvcResult = mockMvc.perform(post(requestURL).with(csrf()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        VoteResult voteResult = objectMapper.readValue(json, VoteResult.class);

        assertTrue(voteResult.isSuccessful());
        assertThat(voteResult.getMessage()).contains("unvoted up");

        int voteCountAfter = voteResult.getVoteCount();
        assertEquals(voteCountBefore - 1, voteCountAfter);
    }
}