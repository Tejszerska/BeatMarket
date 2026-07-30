package com.spring.beatmarket.domain.licensing;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
class SongPriceId implements Serializable {
    private Long songId;
    private LicenseTier tier;

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        final SongPriceId that = (SongPriceId) o;
        return Objects.equals(songId, that.songId) && tier == that.tier;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(songId);
        result = 31 * result + Objects.hashCode(tier);
        return result;
    }
}
