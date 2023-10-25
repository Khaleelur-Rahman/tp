package seedu.address.model.event;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.address.Address;
import seedu.address.model.name.Name;
import seedu.address.model.person.Person;

/**
 * Represents an Event in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Event {

    // Identity fields
    private final Name name;

    // Data fields
    private final EventDate date;
    private final Address address;
    private final Set<Person> contacts = new HashSet<>();

    /**
     * Constructs a {@code Event}.
     *
     * @param name A valid name.
     * @param date A valid date.
     * @param address A valid address.
     * @param contacts A valid set of contacts.
     */
    public Event(Name name, EventDate date, Address address, Set<Person> contacts) {
        requireAllNonNull(name, date, address, contacts);
        this.name = name;
        this.date = date;
        this.address = address;
        this.contacts.addAll(contacts);
    }

    public Address getAddress() {
        return address;
    }

    public Name getName() {
        return name;
    }

    public EventDate getDate() {
        return date;
    }

    /**
     * Returns an immutable contacts set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Person> getContacts() {
        return Collections.unmodifiableSet(contacts);
    }

    /**
     * Checks whether this {@code Event} is linked to the given {@code Person}.
     * @param contact The {@code Person} to be checked with.
     * @return {@code true} if this {@code Event} is linked to the given {@code Person}
     *     and {@code false} otherwise.
     */
    public boolean isLinkedToContact(Person contact) {
        return contacts.stream().anyMatch(contact::isSamePerson);
    }

    /**
     * Links the given {@code Person} to this {@code Event}.
     * @param toLink The {@code Person} to be linked to this {@code Event}.
     */
    public void linkContact(Person toLink) {
        contacts.add(toLink);
    }

    /**
     * Returns true if both events have the same name.
     * This defines a weaker notion of equality between two events.
     */
    public boolean isSameEvent(Event otherEvent) {
        if (otherEvent == this) {
            return true;
        }

        return otherEvent != null
                && (otherEvent.getName().equals(getName()));
    }

    /**
     * Returns true if both events have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Event)) {
            return false;
        }

        Event otherEvent = (Event) other;
        return name.equals(otherEvent.name)
                && date.equals(otherEvent.date)
                && address.equals(otherEvent.address)
                && contacts.equals(otherEvent.contacts);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, date, address, contacts);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("date", date)
                .add("address", address)
                .add("contacts", contacts)
                .toString();
    }

}
