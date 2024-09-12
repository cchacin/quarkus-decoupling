package org.acme;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@DisplayName("People Service")
class PeopleServiceTest implements WithAssertions {

    static final Person PERSON_1 = new Person(
            1L,
            "Person 1",
            LocalDate.of(2000, 1, 1),
            Person.Status.Alive
    );

    @Nested
    @DisplayName("When get(id) is called")
    class GetPeopleById1 {

        @Nested
        @DisplayName("And cache contains person 1")
        class CacheContainsPerson1 {

            @Test
            @DisplayName("It should return what is the cache")
            void should_Return_What_Is_In_The_Cache() {
                // Given
                var cache = new HashMap<Long, Person>();
                var database = new HashMap<Long, Person>();

                var peopleResource = new PeopleService(
                        (Long id) -> Optional.ofNullable(cache.get(id)),
                        (Long id) -> Optional.ofNullable(database.get(id)),
                        (Person person) -> cache.put(person.getId(), person),
                        (Person person) -> database.put(person.getId(), person)
                );
                cache.put(1L, PERSON_1);

                // When
                var result = peopleResource.get(1L);

                // Then
                assertThat(result).isNotNull().contains(PERSON_1);
                assertThat(cache).containsExactlyEntriesOf(Map.of(PERSON_1.getId(), PERSON_1));
                assertThat(database).isEmpty();
            }
        }

        @Nested
        @DisplayName("And cache does not contain person 1")
        class CacheDoesNotContainPerson1 {

            @Nested
            @DisplayName("And database contains person 1")
            class DatabaseContainsPerson1 {

                @Test
                @DisplayName("It should return person 1 from the database and store it in cache")
                void should_Return_Person1_From_Database_And_Store_It_In_Cache() {
                    // Given
                    var cache = new HashMap<Long, Person>();
                    var database = new HashMap<Long, Person>();

                    var peopleResource = new PeopleService(
                            (Long id) -> Optional.ofNullable(cache.get(id)),
                            (Long id) -> Optional.ofNullable(database.get(id)),
                            (Person person) -> cache.put(person.getId(), person),
                            (Person person) -> database.put(person.getId(), person)
                    );
                    database.put(1L, PERSON_1);

                    // When
                    var result = peopleResource.get(1L);

                    // Then
                    assertThat(result).isNotNull().contains(PERSON_1);
                    assertThat(cache).containsExactlyEntriesOf(Map.of(PERSON_1.getId(), PERSON_1));
                    assertThat(database).containsExactlyEntriesOf(Map.of(PERSON_1.getId(), PERSON_1));
                }
            }

            @Nested
            @DisplayName("And database does not contain person 1")
            class DatabaseDoesNotContainsPerson1 {

                @Test
                @DisplayName("It should return empty")
                void should_Return_Empty() {
                    // Given
                    var cache = new HashMap<Long, Person>();
                    var database = new HashMap<Long, Person>();

                    var peopleResource = new PeopleService(
                            (Long id) -> Optional.ofNullable(cache.get(id)),
                            (Long id) -> Optional.ofNullable(database.get(id)),
                            (Person person) -> cache.put(person.getId(), person),
                            (Person person) -> database.put(person.getId(), person)
                    );

                    // When
                    var result = peopleResource.get(1L);

                    // Then
                    assertThat(result).isNotNull().isEmpty();
                    assertThat(cache).isEmpty();
                    assertThat(database).isEmpty();
                }
            }
        }
    }
}
