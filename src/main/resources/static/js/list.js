$(document).ready(function() {
    loadBooks();

    $('#addBookForm').submit(function(event) {
        event.preventDefault();
        const bookData = {
            title: $('#title').val(),
            author: $('#author').val(),
            price: $('#price').val(),
            category: { id: $('#category').val() }
        };
        addBook(bookData);
    });

    $('#editBookForm').submit(function(event) {
        event.preventDefault();
        const bookId = $('#editBookId').val();
        const bookData = {
            title: $('#editTitle').val(),
            author: $('#editAuthor').val(),
            price: $('#editPrice').val(),
            category: { id: $('#editCategory').val() }
        };
        editBook(bookId, bookData);
    });

    $('#addBookModal').on('show.bs.modal', function() {
        loadCategories('category');
    });

    $('#editBookModal').on('show.bs.modal', function() {
        loadCategories('editCategory');
    });
});

function loadCategories(selectId) {
    $.ajax({
        url: 'http://localhost:8080/api/v1/categories',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            const selectElement = $('#' + selectId);
            selectElement.empty();
            $.each(data, function(i, category) {
                selectElement.append($('<option>', {
                    value: category.id,
                    text: category.name
                }));
            });
        },
        error: function() {
            alert('Failed to load categories');
        }
    });
}

function loadBooks() {
    $.ajax({
        url: 'http://localhost:8080/api/v1/books',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            const tbody = $('#book-table-body');
            tbody.empty();
            $.each(data, function(i, book) {
                const editButton = `<button class="btn btn-warning" onclick="showEditForm(${book.id}, '${book.title}', '${book.author}', ${book.price}, '${book.category.id}')">Edit</button>`;
                const deleteButton = `<button class="btn btn-danger" onclick="apiDeleteBook(${book.id})">Delete</button>`;
                let actions = '';
                if (userRole === 'ADMIN') {
                    actions = `
                        <div>
                            ${editButton}
                            ${deleteButton}
                        </div>`;
                }
                tbody.append(`
                    <tr id="book-${book.id}">
                        <td>${book.id}</td>
                        <td>${book.title}</td>
                        <td>${book.author}</td>
                        <td>${book.price}</td>
                        <td>${book.category}</td>
                        <td>
                            ${actions}
                        </td>
                    </tr>
                `);
            });
        },
        error: function() {
            alert('Failed to load books');
        }
    });
}

function addBook(bookData) {
    $.ajax({
        url: 'http://localhost:8080/api/v1/books',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(bookData),
        success: function() {
            alert('Book added successfully');
            loadBooks();
            $('#addBookModal').modal('hide');
            $('#addBookForm')[0].reset();
        },
        error: function() {
            alert('Failed to add book');
        }
    });
}

function editBook(id, bookData) {
    $.ajax({
        url: 'http://localhost:8080/api/v1/books/' + id,
        type: 'PUT',
         dataType: 'json',
        contentType: 'application/json',
        data: JSON.stringify({
            title: bookData.title,
            author: bookData.author,
            price: bookData.price,
            categoryId: bookData.categoryId
        }),
        success: function() {
            alert('Book updated successfully');
            loadBooks();
            $('#editBookModal').modal('hide');
        },
        error: function() {
            alert('Failed to update book');
        }
    });
}


function showEditForm(id, title, author, price, category) {
    $('#editBookId').val(id);
    $('#editTitle').val(title);
    $('#editAuthor').val(author);
    $('#editPrice').val(price);
    $('#editCategory').val(category);
    $('#editBookModal').modal('show');
}

function apiDeleteBook(id) {
    if (confirm('Are you sure you want to delete?')) {
        $.ajax({
            url: 'http://localhost:8080/api/v1/books/' + id,
            type: 'DELETE',
            success: function() {
                alert('Book deleted successfully');
                $('#book-' + id).remove();
            },
            error: function() {
                alert('Failed to delete book');
            }
        });
    }
}
