import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

// Book Class
class Book implements Serializable, Comparable<Book> {
    String title;
    String author;
    boolean isAvailable;
    Queue<Integer> reservationQueue;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.reservationQueue = new LinkedList<>();
    }

    public void lendBook(int memberId) {
        if (isAvailable) {
            isAvailable = false;
            System.out.println("Book lent to Member ID: " + memberId);
        } else {
            reservationQueue.add(memberId);
            System.out.println("Book not available. Member ID " + memberId + " added to waitlist.");
        }
    }

    public void returnBook() {
        if (!reservationQueue.isEmpty()) {
            int nextMemberId = reservationQueue.poll();
            System.out.println("Book lent to Member ID: " + nextMemberId);
        } else {
            isAvailable = true;
            System.out.println("Book returned and is now available.");
        }
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Author: " + author + ", Available: " + (isAvailable ? "Yes" : "No");
    }

    // Implementing compareTo to sort books by title
    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }
}

// Member Class
class Member implements Serializable {
    int id;
    String name;

    public Member(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Member ID: " + id + ", Name: " + name;
    }
}

// Library Class
class Library {
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();
    private static final String BOOK_FILE = "books.dat";
    private static final String MEMBER_FILE = "members.dat";

    public Library() {
        loadBooks();
        loadMembers();
    }

    public void addBook(String title, String author) {
        books.add(new Book(title, author));
        saveBooks();
    }

    public void removeBook(String title) {
        books.removeIf(book -> book.title.equalsIgnoreCase(title));
        saveBooks();
    }

    public Book searchBook(String title) {
        for (Book book : books) {
            if (book.title.equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    public void lendBook(String title, int memberId) {
        Book book = searchBook(title);
        if (book != null) {
            book.lendBook(memberId);
            saveBooks();
        }
    }

    public void returnBook(String title) {
        Book book = searchBook(title);
        if (book != null) {
            book.returnBook();
            saveBooks();
        }
    }

    public void addMember(int id, String name) {
        members.add(new Member(id, name));
        saveMembers();
    }

    public void removeMember(int id) {
        members.removeIf(member -> member.id == id);
        saveMembers();
    }

    public Member searchMemberById(int id) {
        for (Member member : members) {
            if (member.id == id) {
                return member;
            }
        }
        return null;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    private void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(BOOK_FILE))) {
            out.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(BOOK_FILE))) {
            books = (ArrayList<Book>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Starting with an empty book list.");
        }
    }

    private void saveMembers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(MEMBER_FILE))) {
            out.writeObject(members);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMembers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(MEMBER_FILE))) {
            members = (ArrayList<Member>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Starting with an empty member list.");
        }
    }
}

// Main GUI Class
public class LibraryManagementSystemGUI extends JFrame {
    private Library library = new Library();

    public LibraryManagementSystemGUI() {
        setTitle("Library Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 153, 204));
        JLabel titleLabel = new JLabel("Library Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 2, 10, 10));
        
        JButton addBookBtn = new JButton("Add Book");
        JButton removeBookBtn = new JButton("Remove Book");
        JButton searchBookBtn = new JButton("Search Book");
        JButton displayBooksBtn = new JButton("Display Books");
        JButton lendBookBtn = new JButton("Lend Book");
        JButton returnBookBtn = new JButton("Return Book");
        
        JButton addMemberBtn = new JButton("Add Member");
        JButton removeMemberBtn = new JButton("Remove Member");
        JButton searchMemberBtn = new JButton("Search Member");
        JButton displayMembersBtn = new JButton("Display Members");
        
        buttonPanel.add(addBookBtn);
        buttonPanel.add(removeBookBtn);
        buttonPanel.add(searchBookBtn);
        buttonPanel.add(displayBooksBtn);
        buttonPanel.add(lendBookBtn);
        buttonPanel.add(returnBookBtn);
        buttonPanel.add(addMemberBtn);
        buttonPanel.add(removeMemberBtn);
        buttonPanel.add(searchMemberBtn);
        buttonPanel.add(displayMembersBtn);
        
        // Display Panel
        JTextArea displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        
        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        
        // Event Handling
        addBookBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter Book Title:");
            String author = JOptionPane.showInputDialog("Enter Book Author:");
            library.addBook(title, author);
            displayArea.setText("Book added successfully.");
        });

        removeBookBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter Book Title to Remove:");
            library.removeBook(title);
            displayArea.setText("Book removed successfully.");
        });

        searchBookBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter Book Title to Search:");
            Book book = library.searchBook(title);
            if (book != null) {
                displayArea.setText("Book Found: \n" + book);
            } else {
                displayArea.setText("Book not found.");
            }
        });

        displayBooksBtn.addActionListener(e -> {
            displayArea.setText("Books in Library:\n");
            for (Book book : library.getBooks()) {
                displayArea.append(book.toString() + "\n");
            }
        });

        lendBookBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter Book Title to Lend:");
            int memberId = Integer.parseInt(JOptionPane.showInputDialog("Enter Member ID:"));
            library.lendBook(title, memberId);
            displayArea.setText("Lend operation completed.");
        });

        returnBookBtn.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter Book Title to Return:");
            library.returnBook(title);
            displayArea.setText("Return operation completed.");
        });

        addMemberBtn.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Member ID:"));
            String name = JOptionPane.showInputDialog("Enter Member Name:");
            library.addMember(id, name);
            displayArea.setText("Member added successfully.");
        });

        removeMemberBtn.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Member ID to Remove:"));
            library.removeMember(id);
            displayArea.setText("Member removed successfully.");
        });

        searchMemberBtn.addActionListener(e -> {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Member ID to Search:"));
            Member member = library.searchMemberById(id);
            if (member != null) {
                displayArea.setText("Member Found: \n" + member);
            } else {
                displayArea.setText("Member not found.");
            }
        });

        displayMembersBtn.addActionListener(e -> {
            displayArea.setText("Members in Library:\n");
            for (Member member : library.getMembers()) {
                displayArea.append(member.toString() + "\n");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new LibraryManagementSystemGUI();
    }
}
