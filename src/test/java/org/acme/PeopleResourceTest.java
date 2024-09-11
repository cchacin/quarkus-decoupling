package org.acme;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class PeopleResourceTest implements WithAssertions {

    @Test
    @DisplayName("Should return an empty list when repository is empty for GET /people")
    void shouldReturnAnEmptyListWhenRepositoryIsEmptyForGetPeople() {
        // Given
        var repository = mock(PeopleRepository.class);
        when(repository.listAll()).thenReturn(List.of());

        // When
        var result = new PeopleResource(repository).list();

        // Then
        assertThat(result).isEmpty();
        verify(repository, atMostOnce()).listAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should return null when repository is empty for GET /people/{id}")
    void shouldReturnNullWhenRepositoryIsEmptyForGetPeopleId() {
        // Given
        var repository = mock(PeopleRepository.class);
        when(repository.findById(1L)).thenReturn(null);

        // When
        var result = new PeopleResource(repository).get(1L);

        // Then
        assertThat(result).isNull();
        verify(repository, atMostOnce()).findById(1L);
        verifyNoMoreInteractions(repository);
    }

}