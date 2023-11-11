---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# JobFestGo Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

_{ list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well }_

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete_contact 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `EventContactDisplay`, `ContactListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Contact` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete_contact 1")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete_contact 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteContactCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an  `JobFestGoParser` object which in turn creates a parser that matches the command (e.g., `DeleteContactCommandParser`) and uses it to parse the command.
2. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteContactCommand`) which is executed by the `LogicManager`.
3. The command can communicate with the `Model` when it is executed (e.g. to delete a contact).
4. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `JobFestGoParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddContactCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddContactCommand`) which the `JobFestGoParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddContactCommandParser`, `DeleteContactCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.


### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="600" />


The `Model` component,

* stores the JobFestGo data i.e., all `Contact`, `Event`, `Tag`, and `Task` objects which are contained in their respective `UniqueContactList`, `UniqueEventList`, `UniqueTagList`, `UniqueTaskList` objects.
* stores the currently 'selected' `Contact`/`Event`/`Tag`/`Task` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Contact>`/`ObservableList<Event>`/`ObservableList<Tag>`/`ObservableList<Task>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `JobFestGo`, which `Contact` references. This allows `JobFestGo` to only require one `Tag` object per unique tag, instead of each `Contact` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="650" />

The `Storage` component,
* can save both JobFestGo data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `JobFestGoStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Return to home page feature

The home mechanism is facilitated by `JobFestGo` as well as its observable lists for `Contact`, `Event`, as well as `Task`.

The mechanism interacts with both the UI and the lists stored within `JobFestGo`, particularly `unfilteredContacts`, `unfilteredEvents`, and `filteredTasks`.

Given below is an example usage scenario and how the home mechanism behaves at each step.

**Step 1.** The user has input any other command that is not `home`.

**Step 2.** The user executes `home` command to return to the home page.
The `home` command calls upon the creation of `TaskInReminderPredicate` while using `PREDICATE_SHOW_ALL_CONTACTS` and `PREDICATE_SHOW_ALL_EVENTS` to update the respective filtered lists.

The following sequence diagram shows how the select event operation works:

<puml src="diagrams/HomeSequenceDiagram.puml" alt="HomeSequenceDiagram" />

The following activity diagram summarizes what happens when a user executes the home command:

<puml src="diagrams/HomeActivityDiagram.puml" width="325" />

#### Design considerations:

**Aspect: How home is designed**

* **Alternative 1 (current choice):** A home command that users have to type in.
    * Pros: Easy to remember and type since it is only 4 letters. Easier to implement with JobFestGo being CLI based.
    * Cons: Not as intuitive to use as Alternative 2.
    <br></br>
* **Alternative 2:** A home button in the accessibility bar right beside `File` and `Help`.
    * Pros: Even easier to use as only one mouse click is required. No typing is needed.
    * Cons: Harder to implement.

### Select Event Feature

#### Implementation

The select event mechanism is facilitated by `JobFestGo` as well as its observable lists for `Contact`, `Event`, as well as `Task`.

The mechanism interacts with both the UI and the lists stored within `JobFestGo`, particularly `filteredContacts`, `filteredEvents`, and `filteredTasks`.

Given below is an example usage scenario and how the select event mechanism behaves at each step.

**Step 1.** The user launches the application for the first time. The `JobFestGo` will be initialized with the initial address book state.

**Step 2.** The user executes `select_event 1` command to select the 1st event in JobFestGo. The `select_event` command calls upon the creation of `ContactIsInEventPredicate` and `TaskIsInEventPredicate` to update the respective filtered lists.

The following sequence diagram shows how the select event operation works:

<puml src="diagrams/SelectEventSequenceDiagram.puml" alt="SelectEventSequenceDiagram" />

<box type="info" seamless>

**Note:** The lifelines for `SelectEventCommand` and `SelectEventCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

The following activity diagram summarizes what happens when a user executes the select event command:

<puml src="diagrams/SelectEventActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How select event executes**

* **Alternative 1 (current choice):** Highlights the event selected.
  * Pros: Visually appealing.
  * Cons: Slightly harder to implement.
    <br></br>
* **Alternative 2:** Event list gets updated to only show the selected event.
  * Pros: Easy to implement.
  * Cons: Users will have to consistently execute home command to view the other events.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedJobFestGo`. It extends `JobFestGo` with an undo/redo history, stored internally as an `addressBookStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedJobFestGo#commit()` — Saves the current address book state in its history.
* `VersionedJobFestGo#undo()` — Restores the previous address book state from its history.
* `VersionedJobFestGo#redo()` — Restores a previously undone address book state from its history.

These operations are exposed in the `Model` interface as `Model#commitJobFestGo()`, `Model#undoJobFestGo()` and `Model#redoJobFestGo()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedJobFestGo` will be initialized with the initial address book state, and the `currentStatePointer` pointing to that single address book state.

<puml src="diagrams/UndoRedoState0.puml" alt="UndoRedoState0" />

Step 2. The user executes `delete_contact 5` command to delete the 5th contact in the address book. The `delete_contact` command calls `Model#commitJobFestGo()`, causing the modified state of the address book after the `delete_contact 5` command executes to be saved in the `addressBookStateList`, and the `currentStatePointer` is shifted to the newly inserted address book state.

<puml src="diagrams/UndoRedoState1.puml" alt="UndoRedoState1" />

Step 3. The user executes `add_contact n/David …​` to add a new contact. The `add_contact` command also calls `Model#commitJobFestGo()`, causing another modified address book state to be saved into the `addressBookStateList`.

<puml src="diagrams/UndoRedoState2.puml" alt="UndoRedoState2" />

<box type="info" seamless>

**Note:** If a command fails its execution, it will not call `Model#commitJobFestGo()`, so the address book state will not be saved into the `addressBookStateList`.

</box>

Step 4. The user now decides that adding the contact was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoJobFestGo()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous address book state, and restores the address book to that state.

<puml src="diagrams/UndoRedoState3.puml" alt="UndoRedoState3" />


<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index 0, pointing to the initial JobFestGo state, then there are no previous JobFestGo states to restore. The `undo` command uses `Model#canUndoJobFestGo()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</box>

The following sequence diagram shows how the undo operation works:

<puml src="diagrams/UndoSequenceDiagram.puml" alt="UndoSequenceDiagram" />

<box type="info" seamless>

**Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</box>

The `redo` command does the opposite — it calls `Model#redoJobFestGo()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the address book to that state.

<box type="info" seamless>

**Note:** If the `currentStatePointer` is at index `addressBookStateList.size() - 1`, pointing to the latest address book state, then there are no undone JobFestGo states to restore. The `redo` command uses `Model#canRedoJobFestGo()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</box>

Step 5. The user then decides to execute the command `view_contacts`. Commands that do not modify the address book, such as `view_contacts`, will usually not call `Model#commitJobFestGo()`, `Model#undoJobFestGo()` or `Model#redoJobFestGo()`. Thus, the `addressBookStateList` remains unchanged.

<puml src="diagrams/UndoRedoState4.puml" alt="UndoRedoState4" />

Step 6. The user executes `clear`, which calls `Model#commitJobFestGo()`. Since the `currentStatePointer` is not pointing at the end of the `addressBookStateList`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add_contact n/David …​` command. This is the behavior that most modern desktop applications follow.

<puml src="diagrams/UndoRedoState5.puml" alt="UndoRedoState5" />

The following activity diagram summarizes what happens when a user executes a new command:

<puml src="diagrams/CommitActivityDiagram.puml" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete_contact`, just save the contact being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:
* is a job festival event planner
* has a need to manage a significant number of contacts of different types (e.g. vendors, customers) and tasks for different events
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: Each event planner has multiple events, each of which can have a large number of contacts and tasks associated and searching for contact would be a hassle. Our product provides a centralised system that would help job event planners organise their contact information and tasks for quick and easy access.

### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​                                    | I want to …​                           | So that I can…​                                                                             |
|----------|--------------------------------------------|----------------------------------------|---------------------------------------------------------------------------------------------|
| `* * *`  | job fest event planner                     | usage instructions                     | refer to instructions when I forget how to use JobFestGo                                    |
| `* * *`  | job fest event planner                     | add a new contact                      |                                                                                             |
| `* * *`  | job fest event planner                                       | delete a contact                       | remove contacts that I no longer need                                                       |
| `* * *`  | job fest event planner                                       | find a contact by name                 | locate details of contacts without having to go through the entire list                     |
| `* * *`  | job fest event planner                     | view the entire contact list           |                                                                                             |
| `* *`    | job fest event planner                     | add tags                               | add to the pool of roles already available                                                  |
| `* *`    | job fest event planner                     | view all tags                          |                                                                                             |
| `* *`     | job fest event planner                     | delete tags                            | can easily identify who I should be cold calling among my contacts without unnecessary tags |
| `* *`     | job fest event planner                     | filter contacts by tags                | conveniently view all the contacts tagged by specific tags                                  |
| `* *`    | job fest event planner                        | add a new event                        | keep track of the events I have to plan                                                     |
| `* *`     | job fest event planner                     | view all events                        | remember all the events I am involved in so far                                             |
| `* *`    | job fest event planner                        | delete events                          | remove events I no longer need                                                              |
| `* *`     | job fest event planner                     | select events                          | can easily view the contacts and tasks to do for each particular event                      |
| `* *`     | job fest event planner                     | link contacts to events                | remember which event the specific contacts are involved in                                  |
| `* *`    | job fest event planner                        | add a new task for an event            | remember the tasks I need to do for the event                                               |
| `* *`    | job fest event planner                        | delete a task for an event             | remove the irrelevant and completed tasks from an event                                     |
| `* *`    | job fest event planner                        | mark a task as completed for an event  | keep track of the completed tasks                                                           |
| `* *`    | job fest event planner                        | mark a task as incomplete for an event | keep track of the tasks that are not completed                                              |
| `* *`    | job fest event planner                        | view the tasks due soon                | prioritise the tasks according to its deadline                                              |
| `* *`    | job fest event planner                        | clear all the data                     | start a new event planning process with new data                                            |
| `* *`      | job fest event planner | return to the home page                |                                                                                             |

*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `JobFestGo` and the **Actor** is the `user`, unless specified otherwise)


**Use case: Add a contact**

**MSS**

1.  User requests to add contact and specifies details of contact
2.  JobFestGo adds the contact to list of contacts
3.  JobFestGo shows updated list of contacts

    Use case ends.

**Extensions**

* 1a. Any of the mandatory fields not specified

  * 1a1. JobFestGo informs user that mandatory fields not specified

    Use case ends.

* 1b. Phone number already exists.

    * 1b1. JobFestGo informs user that phone number already exists.

      Use case ends.

* 1c. Email is in incorrect format.

    * 1c1. JobFestGo informs user that email is in wrong format.

      Use case ends.

* 1d. Phone number is in incorrect format.

    * 1d1. JobFestGo informs user that phone number is in wrong format.

      Use case ends.

* 1e. Tag is not found in list.

    * 1e1. JobFestGo informs user that tag input is not in tag List.

      Use case ends.

**Use case: Delete a contact**

**MSS**

1.  User requests to list contacts
2.  JobFestGo shows a list of contacts
3.  User requests to delete a specific contact in the list
4.  JobFestGo deletes the contact

   Use case ends.

**Extensions**

* 3a. The given index is invalid.

    * 3a1. JobFestGo shows an error message.

      Use case resumes at step 2.

* 3b. Missing index.

    * 3b1. JobFestGo shows an error message.

      Use case resumes at step 2.

**Use case: List all contacts**

**MSS**

1.  User requests to list all contacts
2.  JobFestGo shows a list of all contacts

    Use case ends.


**Use case: Add a tag**

**MSS**

1.  User requests to add tag
2.  User keys in the tag to add to the collection of tags
3.  JobFestGo adds the tag

   Use case ends.

**Extensions**
* 2a. Missing `t/` in the command.
    * 2a1. JobFestGo shows an error message.

      Use case resumes at step 2.

* 2b. Missing tag name.
    * 2b1. JobFestGo shows an error message.

      Use case resumes at step 2.

* 3a. The given tag name is already in the tag list.

    * 3a1. JobFestGo shows an error message.

      Use case resumes at step 2.

**Use case: View all tags**

**MSS**
1. User requests to list tags
2. JobFestGo shows a list of all tags

   Use case ends.

**Use case: Delete a tag**

**MSS**

1. User requests to list the tags
2. JobFestGo shows a list of tags
3. User requests to delete a specified tag in the list
4. JobFestGo deletes the tag

      Use case ends.

**Extensions**

* 2a. The list is empty.

   Use case ends.

* 3a. The given tag name is invalid.

    * 3a1. JobFestGo shows an error message.

      Use case resumes at step 2.

* 3b. Missing `t/` in the command.

    * 3b1. JobFestGo shows an error message.

      Use case resumes at step 2.

* 3c. Missing tag name.

    * 3c1. JobFestGo shows an error message.

      Use case resumes at step 2.

**Use case: Filter contacts by tag**

**MSS**
1. User requests to filter contacts by tags
2. JobFestGo shows a list of contacts tagged by specified tags

   Use case ends.

**Extensions**
* 1a. The given tag name is invalid.

    * 1a1. JobFestGo shows an error message.

      Use case resumes at step 1.

**Use case: Add an event**

**MSS**

1. User requests to add event
2. User specifies the details of the event
3. JobFestGo adds the event to collection of events
4. JobFestGo shows updated list of events

   Use case ends.

**Extensions**

* 1a. Any of the mandatory fields not specified

    * 1a1. JobFestGo informs user that mandatory fields not specified

      Use case ends.

* 1b. Event name already exists.

    * 1b1. JobFestGo informs user that event name already exists.

      Use case ends.

* 1c. Date is in incorrect format.

    * 1c1. JobFestGo informs user that date is in wrong format.

      Use case ends.

* 1d. Date is before current date.

    * 1d1. JobFestGo informs user that date is before current date.

      Use case ends.

**Use case: View all events**

**MSS**
1. User requests to list events
2. JobFestGo shows a list of all events

   Use case ends.

**Use case: Delete an event**

**MSS**

1. JobFestGo shows a list of events
2. User requests to delete a specified event in the list
3. JobFestGo deletes the event
4. JobFestGo displays the updated events

      Use case ends.

**Extensions**

* 2a. The event list is empty.

   Use case ends.

* 3a. The given index is invalid.

    * 3a1. JobFestGo shows an error message.

      Use case resumes at step 2.

* 3b. Missing index in the command.

    * 3b1. JobFestGo shows an error message.

      Use case resumes at step 2.

**Use case: Select an event**

**MSS**

1. User requests to select an event
2. JobFestGo displays the contacts and tasks related to the event
3. User <u>adds a task</u> or <u>deletes a task</u> or <u>marks a task</u> or <u>unmarks a task</u>.

      Use case ends.


**Extensions**

* 1a. The given index is invalid.

    * 1a1. JobFestGo shows an error message.

      Use case resumes at step 1.

* 1b. Missing index.

    * 1b1. JobFestGo shows an error message.

      Use case resumes at step 1.

**Use case: Link contacts to an event**

**MSS**
1. User requests to link specified contacts to a specified event.
2. JobFestGo links the contacts to the event.

   Use case ends.

**Extensions**
* 1a. The given event does not exist.

    * 1a1. JobFestGo shows an error message.

      Use case resumes at step 1.

* 1b. One of the given contacts does not exist.

    * 1b1. JobFestGo shows an error message.

      Use case resumes at step 1.

* 1a. One of the given contacts is already linked to the given event.

    * 1a1. JobFestGo shows an error message.

      Use case resumes at step 1.

**Use case: Add a task**

**MSS**

1.  User requests to add task and specifies details of task with the event to be added in
2.  JobFestGo adds the task to list of tasks
3.  JobFestGo adds the task to the event specified
4.  JobFestGo shows updated list of tasks

    Use case ends.

**Extensions**

* 1a. Any of the mandatory fields not specified

  * 1a1. JobFestGo informs user that mandatory fields not specified

    Use case ends.

* 1b. Date is invalid.

    * 1b1. JobFestGo informs user that date is invalid.

      Use case ends.

* 1c. Event does not already exist.

    * 1c1. JobFestGo informs user that event does not already exist.

      Use case ends.

**Use case: Return to the home page**

**MSS**
1. User is on any page
2. User requests to return to the home page
3. JobFestGo returns user to the home page

   Use case ends.

**Extensions**

* 2a. The user is already on the home page

   Use case ends.

*{More to be added}*

---
### Non-Functional Requirements
1. Environment requirement:
* Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2. Scalability:
* Should be able to hold up to 1000 contacts without a noticeable sluggishness in performance for typical usage.
3. Usability:
* A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
* The user interface should be intuitive for event planners to use, for non-tech savy job event planners
4. Performance:
* The system should respond to all inputs within a reasonable time frame (within 2 seconds)

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X
* **Private contact detail**: A contact detail that is not meant to be shared with others

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a contact

1. Deleting a contact while all contacts are being shown

   1. Prerequisites: List all contacts using the `view_contacts` command. Multiple contacts in the list.

   1. Test case: `delete_contact 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `delete_contact 0`<br>
      Expected: No contact is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `delete_contact`, `delete_contact x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
