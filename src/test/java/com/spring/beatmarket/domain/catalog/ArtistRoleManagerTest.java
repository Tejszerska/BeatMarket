package com.spring.beatmarket.domain.catalog;

import com.spring.beatmarket.domain.catalog.exception.MainRoleAbsentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Testing only complicated internal logic and edge cases to avoid mock hell
 * and bloated setup in Facade tests.
 * <p>
 * Standard paths are covered by {@link ArtistFacadeTest}, {@link AlbumFacadeTest}
 * and {@link SongFacadeTest}.
 */
class ArtistRoleManagerTest {

    private final RoleValidator roleValidator = new RoleValidator();
    private final ArtistRetriever artistRetriever = mock(ArtistRetriever.class);

    private final ArtistRoleManager artistRoleManager = new ArtistRoleManager(roleValidator, artistRetriever);

    @Test
    @DisplayName("Should sync Artists when initial collection was null")
    void should_sync_when_init_collection_null() {
        //given
        Artist a1 = TestObjectsFactory.createArtistWithId(10L, "A1");
        Artist a2 = TestObjectsFactory.createArtistWithId(20L, "A2");
        Artist a3 = TestObjectsFactory.createArtistWithId(21L, "A3");
        Artist a4 = TestObjectsFactory.createArtistWithId(22L, "A4");
        Optional<Long> mainArtistId = Optional.of(10L);
        Optional<List<Long>> featArtistIds = Optional.of(List.of(20L, 21L, 22L));
        String subjectEntityName = "Tested";
        List<Artist> currentArtists = null;

        Map<Artist, Boolean> assignedRoles = new HashMap<>();
        assertThat(currentArtists).isNull();
        Runnable clearAction = assignedRoles::clear;
        BiConsumer<Artist, Boolean> assignAction = assignedRoles::put;
        Mockito.when(artistRetriever.getActives(Set.of(10L, 20L, 21L, 22L)))
                .thenReturn(List.of(a1, a2, a3, a4));

        //when
        artistRoleManager.sync(mainArtistId, featArtistIds, subjectEntityName,
                currentArtists, clearAction, assignAction);
        //then
        assertThat(assignedRoles).hasSize(4);
        assertThat(assignedRoles).containsEntry(a1, true);
        assertThat(assignedRoles).containsEntry(a2, false);
        assertThat(assignedRoles).containsEntry(a3, false);
        assertThat(assignedRoles).containsEntry(a4, false);
    }

    @Test
    @DisplayName("Should sync Artists when main Artist is initially present")
    void should_sync_when_init_main_artist_present() {
        //given
        Artist a1 = TestObjectsFactory.createArtistWithId(10L, "A1");
        Artist a2 = TestObjectsFactory.createArtistWithId(20L, "A2");
        Artist a3 = TestObjectsFactory.createArtistWithId(21L, "A3");
        Artist a4 = TestObjectsFactory.createArtistWithId(22L, "A4");

        Optional<Long> mainArtistId = Optional.of(20L);
        Optional<List<Long>> featArtistIds = Optional.of(List.of(21L, 22L));
        String subjectEntityName = "Tested";
        List<Artist> currentArtists = List.of(a1);

        assertThat(currentArtists).hasSize(1);

        Map<Artist, Boolean> assignedRoles = new HashMap<>();
        assignedRoles.put(a1, true);

        Runnable clearAction = assignedRoles::clear;
        BiConsumer<Artist, Boolean> assignAction = assignedRoles::put;
        Mockito.when(artistRetriever.getActives(Set.of(20L, 21L, 22L)))
                .thenReturn(List.of(a2, a3, a4));

        //when
        artistRoleManager.sync(mainArtistId, featArtistIds, subjectEntityName,
                currentArtists, clearAction, assignAction);
        //then
        assertThat(assignedRoles).hasSize(3);
        assertThat(assignedRoles).doesNotContainEntry(a1, true);
        assertThat(assignedRoles).containsEntry(a2, true);
        assertThat(assignedRoles).containsEntry(a3, false);
        assertThat(assignedRoles).containsEntry(a4, false);
    }


    @Test
    @DisplayName("Should sync Artists when main artists is not being updated," +
            " but present in original (mainArtistId = null)")
    void should_sync_when_mainArtistId_is_null() {
        //given
        Artist a1 = TestObjectsFactory.createArtistWithId(10L, "A1");
        Artist a2 = TestObjectsFactory.createArtistWithId(20L, "A2");
        Artist a3 = TestObjectsFactory.createArtistWithId(21L, "A3");
        Artist a4 = TestObjectsFactory.createArtistWithId(22L, "A4");

        Optional<Long> mainArtistId = null;
        Optional<List<Long>> featArtistIds = Optional.of(List.of(21L, 22L));
        String subjectEntityName = "Tested";
        List<Artist> currentArtists = List.of(a1);

        assertThat(currentArtists).hasSize(1);

        Map<Artist, Boolean> assignedRoles = new HashMap<>();
        assignedRoles.put(a1, true);

        Runnable clearAction = assignedRoles::clear;
        BiConsumer<Artist, Boolean> assignAction = assignedRoles::put;
        Mockito.when(artistRetriever.getActives(Set.of(10L, 21L, 22L)))
                .thenReturn(List.of(a1, a3, a4));

        //when
        artistRoleManager.sync(mainArtistId, featArtistIds, subjectEntityName,
                currentArtists, clearAction, assignAction);
        //then
        assertThat(assignedRoles).hasSize(3);
        assertThat(assignedRoles).containsEntry(a1, true);
        assertThat(assignedRoles).containsEntry(a3, false);
        assertThat(assignedRoles).containsEntry(a4, false);
    }

    @Test
    @DisplayName("Should only update main, when feat are not updated (featArtistIds=null)")
    void should_update_only_main() {
        //given
        Artist a1 = TestObjectsFactory.createArtistWithId(10L, "A1");
        Artist a2 = TestObjectsFactory.createArtistWithId(20L, "A2");
        Artist a3 = TestObjectsFactory.createArtistWithId(30L, "A3");

        Optional<Long> mainArtistId = Optional.of(30L);

        String subjectEntityName = "Tested";
        List<Artist> currentArtists = List.of(a1, a2);
        Optional<List<Long>> featArtistIds = null;
        assertThat(currentArtists).hasSize(2);

        Map<Artist, Boolean> assignedRoles = new HashMap<>();
        assignedRoles.put(a1, true);
        assignedRoles.put(a2, false);

        Runnable clearAction = assignedRoles::clear;
        BiConsumer<Artist, Boolean> assignAction = assignedRoles::put;
        Mockito.when(artistRetriever.getActives(Set.of(30L, 20L)))
                .thenReturn(List.of(a3, a2));

        //when
        artistRoleManager.sync(mainArtistId, featArtistIds, subjectEntityName,
                currentArtists, clearAction, assignAction);
        //then
        assertThat(assignedRoles).hasSize(2);
        assertThat(assignedRoles).doesNotContainEntry(a1, true);
        assertThat(assignedRoles).containsEntry(a2, false);
        assertThat(assignedRoles).containsEntry(a3, true);
    }

    @Test
    @DisplayName("Should throw MainRoleAbsentException when updating to add only featured without main initially")
    void should_throw_MainRoleAbsentException_when_no_main() {
        //given
        Artist a3 = TestObjectsFactory.createArtistWithId(21L, "A3");
        Artist a4 = TestObjectsFactory.createArtistWithId(22L, "A4");

        Optional<Long> mainArtistId = null;
        Optional<List<Long>> featArtistIds = Optional.of(List.of(21L, 22L));
        String subjectEntityName = "Test";
        List<Artist> currentArtists = List.of();

        assertThat(currentArtists).isEmpty();

        Map<Artist, Boolean> assignedRoles = new HashMap<>();

        Runnable clearAction = assignedRoles::clear;
        BiConsumer<Artist, Boolean> assignAction = assignedRoles::put;
        Mockito.when(artistRetriever.getActives(Set.of(21L, 22L)))
                .thenReturn(List.of(a3, a4));

        //when & then
        assertThatThrownBy(() -> artistRoleManager.sync(mainArtistId, featArtistIds, subjectEntityName,
                currentArtists, clearAction, assignAction))
                .isInstanceOf(MainRoleAbsentException.class)
                .hasMessage("Cannot update Tests featured artists when main artist isn't specified.");
    }

    @Test
    @DisplayName("Should clear artists list, when target main id = null and target feat collection is empty")
    void should_update_only_main_when_no_feat() {
        //given
        Artist a1 = TestObjectsFactory.createArtistWithId(10L, "A1");
        Artist a2 = TestObjectsFactory.createArtistWithId(20L, "A2");
        Artist a3 = TestObjectsFactory.createArtistWithId(30L, "A3");

        Optional<Long> mainArtistId = Optional.empty();

        String subjectEntityName = "Tested";
        List<Artist> currentArtists = List.of(a1, a2);
        Optional<List<Long>> featArtistIds = Optional.empty();
        assertThat(currentArtists).hasSize(2);

        Map<Artist, Boolean> assignedRoles = new HashMap<>();
        assignedRoles.put(a1, true);
        assignedRoles.put(a2, false);

        Runnable clearAction = assignedRoles::clear;
        BiConsumer<Artist, Boolean> assignAction = assignedRoles::put;
        Mockito.when(artistRetriever.getActives(Set.of(30L)))
                .thenReturn(List.of(a3));

        //when
        artistRoleManager.sync(mainArtistId, featArtistIds, subjectEntityName,
                currentArtists, clearAction, assignAction);
        //then
        assertThat(assignedRoles).isEmpty();
        assertThat(assignedRoles).doesNotContainEntry(a1, true);
        assertThat(assignedRoles).doesNotContainEntry(a2, false);
    }

}
