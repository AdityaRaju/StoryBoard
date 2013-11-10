$(function(){

    //e.preventDefault();
    var dataObj = {};
    var mainDivId = '#'+'article'+'';
    dataObj['q']=$(mainDivId).html();

    $.post('/1/eswidget/find', { data:JSON.stringify(dataObj)}, function (data) {
        console.log(data);
        //iterate data...
        var newHTML = $(mainDivId).html();
        $.each( data.data, function( key, value ) {
            console.log( key + ": " + value );
            var regExp = new RegExp(key,"i");
            newHTML=newHTML.replace(regExp,'<span class="es-ticker" n-symbol="' + (value ? value:'')+
                '">' + key.toUpperCase() +
                '</span>')
        });
        $(mainDivId).html(newHTML);
        $('span.es-ticker').hover(function(e) {
            //hideAllPopovers();
            e.preventDefault();
            var tickerSymbol = $(this).attr('n-symbol');
            console.log(' hover -->'+tickerSymbol);
            var e1=$(this);
            var newsURLToCall1 = '/1/estimates/for/'+tickerSymbol;

            $.get(newsURLToCall1,function(d) {
                //console.log('output ->'+ d);
                var output = $( "#cInfoTemplate2" ).tmpl( d ).html();
                //console.log('output ->'+output);
                var htmlStr = '<table class="table table-striped"><thead><tr><th>#</th><th>EPS</th><th>Revenue</th></tr></thead><tbody>' ;
                $.each(d,function(index,obj){
                    htmlStr+='<tr><td>' + obj.fiscal_year+"Q"+obj.fiscal_quarter+
                        '</td><td>' +  obj.eps+
                        '</td><td>' + obj.revenue+
                        '</td></tr>';
                })
                //output+
                htmlStr+='</tbody></table>';//
                e1.popover({html : true,
                    live: true,
                    placement:'bottom',title:tickerSymbol,content: htmlStr}).popover('show');
            });
        },function(e) {
            var e1=$(this);
            e1.popover('hide');
        });
        /*for (var m in data.data){
         for (var i=0;i<data.data[m].length;i++){
         var regExp = new RegExp('Twitter',"i");
         $('body').html( $('body').html().replace(regExp,'<span class="es-ticker">Twitter 1</span>') )
         }
         }*/

        //find each element and add custom span tag...

    });



});