    //############################################################
    //Initiating Customer List Table and other by employing 
    //Bootstrap Data Tables.
    // ############################################################
    $(function () {
        $("#customer-list").DataTable({
            "responsive": true, "lengthChange": true, "autoWidth": false,
            "buttons": ["copy", "csv", "excel", "pdf", "print", "colvis"],
            "lengthMenu": [ [5, 10, 20, -1], [5, 10, 20, "All"] ]
        }).buttons().container().appendTo('#customer-list_wrapper .col-md-6:eq(0)');
    });
    $(function () {
        $("#product-list").DataTable({
            "responsive": true, "lengthChange": true, "autoWidth": false,
            "buttons": ["copy", "csv", "excel", "pdf", "print", "colvis"],
            "lengthMenu": [ [5, 10, 20, -1], [5, 10, 20, "All"] ]
        }).buttons().container().appendTo('#product-list_wrapper .col-md-6:eq(0)');
    });
    $(function () {
        $("#order-list").DataTable({
            "responsive": true, "lengthChange": true, "autoWidth": false,
            "buttons": ["copy", "csv", "excel", "pdf", "print", "colvis"],
            "lengthMenu": [ [5, 10, 20, -1], [5, 10, 20, "All"] ]
        }).buttons().container().appendTo('#order-list_wrapper .col-md-6:eq(0)');
    });
    $(function () {
        $("#admin-user-list").DataTable({
            "responsive": true, "lengthChange": true, "autoWidth": false,
            "buttons": ["copy", "csv", "excel", "pdf", "print", "colvis"],
            "lengthMenu": [ [5, 10, 20, -1], [5, 10, 20, "All"] ]
        }).buttons().container().appendTo('#admin-user-list_wrapper .col-md-6:eq(0)');
    });