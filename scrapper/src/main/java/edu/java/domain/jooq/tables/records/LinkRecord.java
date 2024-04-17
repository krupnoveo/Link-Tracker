/*
 * This file is generated by jOOQ.
 */

package edu.java.domain.jooq.tables.records;

import edu.java.domain.jooq.tables.Link;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;
import javax.annotation.processing.Generated;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;

/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.18.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class LinkRecord extends UpdatableRecordImpl<LinkRecord>
    implements Record4<Long, String, OffsetDateTime, OffsetDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a detached LinkRecord
     */
    public LinkRecord() {
        super(Link.LINK);
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    @ConstructorProperties({"id", "url", "updatedAt", "checkedAt"})
    public LinkRecord(
        @Nullable Long id,
        @Nullable String url,
        @Nullable OffsetDateTime updatedAt,
        @Nullable OffsetDateTime checkedAt
    ) {
        super(Link.LINK);

        setId(id);
        setUrl(url);
        setUpdatedAt(updatedAt);
        setCheckedAt(checkedAt);
        resetChangedOnNotNull();
    }

    /**
     * Create a detached, initialised LinkRecord
     */
    public LinkRecord(edu.java.domain.jooq.tables.pojos.Link value) {
        super(Link.LINK);

        if (value != null) {
            setId(value.getId());
            setUrl(value.getUrl());
            setUpdatedAt(value.getUpdatedAt());
            setCheckedAt(value.getCheckedAt());
            resetChangedOnNotNull();
        }
    }

    /**
     * Getter for <code>LINK.ID</code>.
     */
    @Nullable
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>LINK.ID</code>.
     */
    public void setId(@Nullable Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>LINK.URL</code>.
     */
    @Size(max = 1000000000)
    @Nullable
    public String getUrl() {
        return (String) get(1);
    }

    /**
     * Setter for <code>LINK.URL</code>.
     */
    public void setUrl(@Nullable String value) {
        set(1, value);
    }

    /**
     * Getter for <code>LINK.UPDATED_AT</code>.
     */
    @Nullable
    public OffsetDateTime getUpdatedAt() {
        return (OffsetDateTime) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>LINK.UPDATED_AT</code>.
     */
    public void setUpdatedAt(@Nullable OffsetDateTime value) {
        set(2, value);
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>LINK.CHECKED_AT</code>.
     */
    @Nullable
    public OffsetDateTime getCheckedAt() {
        return (OffsetDateTime) get(3);
    }

    /**
     * Setter for <code>LINK.CHECKED_AT</code>.
     */
    public void setCheckedAt(@Nullable OffsetDateTime value) {
        set(3, value);
    }

    @Override
    @NotNull
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    @Override
    @NotNull
    public Row4<Long, String, OffsetDateTime, OffsetDateTime> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    @NotNull
    public Row4<Long, String, OffsetDateTime, OffsetDateTime> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    @NotNull
    public Field<Long> field1() {
        return Link.LINK.ID;
    }

    @Override
    @NotNull
    public Field<String> field2() {
        return Link.LINK.URL;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field3() {
        return Link.LINK.UPDATED_AT;
    }

    @Override
    @NotNull
    public Field<OffsetDateTime> field4() {
        return Link.LINK.CHECKED_AT;
    }

    @Override
    @Nullable
    public Long component1() {
        return getId();
    }

    @Override
    @Nullable
    public String component2() {
        return getUrl();
    }

    @Override
    @Nullable
    public OffsetDateTime component3() {
        return getUpdatedAt();
    }

    @Override
    @Nullable
    public OffsetDateTime component4() {
        return getCheckedAt();
    }

    @Override
    @Nullable
    public Long value1() {
        return getId();
    }

    @Override
    @Nullable
    public String value2() {
        return getUrl();
    }

    @Override
    @Nullable
    public OffsetDateTime value3() {
        return getUpdatedAt();
    }

    @Override
    @Nullable
    public OffsetDateTime value4() {
        return getCheckedAt();
    }

    @Override
    @NotNull
    public LinkRecord value1(@Nullable Long value) {
        setId(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value2(@Nullable String value) {
        setUrl(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    @Override
    @NotNull
    public LinkRecord value3(@Nullable OffsetDateTime value) {
        setUpdatedAt(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord value4(@Nullable OffsetDateTime value) {
        setCheckedAt(value);
        return this;
    }

    @Override
    @NotNull
    public LinkRecord values(
        @Nullable Long value1,
        @Nullable String value2,
        @Nullable OffsetDateTime value3,
        @Nullable OffsetDateTime value4
    ) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }
}
