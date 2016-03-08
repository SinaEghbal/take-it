# MyDiary
## An app which takes care of all your notes

### What is MyDiary

MyDiary is a note-taking app for android platform which lets you take care of
all your notes.

Using MyDiary, you can write text notes, edit them, take picture notes and
organise your notes.

### Design

![MyDiary class diagram](images/MyDiary.png)

The MyDiary app consists of 4 main classess to handle the different tasks. In
what follows, we will explain the abstract design of our MyDiary app.

1. Note: This class takes care of storing all the fields in a note in addition
letting us add, modify and delete our notes.

2. Picture: This is the class responsible for using the android camera service.
Moreover, this class stores images and contains all the methods that our note
application uses to manage the picture notes or notes that contain pictures.

3. ViewNote: This is a subclass of the Activity class which provides the android
GUI of our app. Simply saying, this class contains the front-end implementation
for the view note section of our application.

4. NoteEntry: Like the ViewNote class, this class is a subclass of the Activity
class. It provides us with the front-end implementation of our note entry page
which is used for entering and editing the notes.

