package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.DuplicateRoleException;
import com.spring.beatmarket.domain.catalog.exception.MainRoleRemovalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class RoleValidatorTest {
    private final RoleValidator roleValidator = new RoleValidator();

    @Test
    @DisplayName("Should throw DuplicateRoleException when there are conflicting ids")
    void should_throw_DuplicateRoleException() {
        //given
        Collection<Long> mainIds = List.of(1L, 2L, 3L);
        Collection<Long> featIds = List.of(2L, 3L, 3L);
        String subjectName = "Subject";
        String targetName = "Target";

        //when & then
        assertThatThrownBy(() -> roleValidator.combineAndValidateIds(mainIds, featIds, subjectName, targetName))
                .isInstanceOf(DuplicateRoleException.class)
                .hasMessage("Subject cannot be both main and featured on the same Target")
                .hasFieldOrPropertyWithValue("conflictingIds", Set.of(2L, 3L));
    }

    @Test
    @DisplayName("Should throw MainRoleRemovalException when Artist is main (first on the list)")
    void should_throw_MainRoleRemovalException() {
        //given
        Artist artistMain = TestObjectsFactory.createArtistWithId(1L, "Main Artist");
        Artist artist2 = TestObjectsFactory.createArtistWithId(2L, "Artist 2");
        Artist artist3 = TestObjectsFactory.createArtistWithId(3L, "Artist 3");

        List<Artist> artistList = List.of(artistMain, artist2, artist3);
        Long subjectId = 10L;
        String subjectName = "Subject";

        //when & then
        assertThatThrownBy(() -> roleValidator.validateIsMainArtist(artistList, artistMain, subjectId, subjectName))
                .isInstanceOf(MainRoleRemovalException.class)
                .hasMessage("Cannot remove Artist id=1 from Subject id=10 because it has featured artists attached.")
                .hasFieldOrPropertyWithValue("artistId", 1L)
                .hasFieldOrPropertyWithValue("subjectId", 10L);
    }

    @Test
    @DisplayName("Should return false, when list of ids is null")
    void should_return_false() {
        //given
        Artist artistMain = TestObjectsFactory.createArtistWithId(1L, "Main Artist");
        Artist artist2 = TestObjectsFactory.createArtistWithId(2L, "Artist 2");

        List<Artist> artists = List.of(artistMain, artist2);
        List<Long> ids = null;
        Long checkedId = 1L;
        String subjectName = "Subject";

        //when
        boolean result = roleValidator.validateHasMainArtist(ids, checkedId, artists, subjectName);

        //then
        assertThat(result).isFalse();
    }
}
