    //############################################################
    //Initiating Customer List Table by employing 
    //Bootstrap Data Tables.
    // ############################################################
    $(document).ready( function () {
        $('#customerlist').DataTable({ 
        // Specify multiple classes to be used
        stripeClasses: ['stripe-1',
                        'stripe-2']});
    } );