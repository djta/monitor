
	var PoolLength = 10;
	var ActiveXPool = new Array();
	var RequestQue = new Array();

	function populatePool()
	{
	for (var i = 0; i < PoolLength; i++)
	{
        if (window.XMLHttpRequest) {
    	   ActiveXPool.push( new XMLHttpRequest() );
    	} else if (window.ActiveXObject) {
    	   ActiveXPool.push( new ActiveXObject("Microsoft.XMLHTTP") );
    	}
	}
	}

	populatePool();

	//function download( callback, href )
	function getResponse ( href1 )
	{
		//alert( href1 );
	  	RequestQue.push( [ href1 ] );
	  	HandleRequest();
	}

	function getFreeRequest()
	{

		for ( var j = 0; j < ActiveXPool.length; j++ )
		{
			if ( ActiveXPool[ j ].readyState == 4 || ActiveXPool[ j ].readyState == 0 )
			{
				return j;
			}
		}
		return -1;
	}

	function HandleRequest( )
	{
		if ( RequestQue[0] )
		{
			var indx = getFreeRequest();
			if (indx > -1)
			{
				var req = RequestQue.shift();
				ActiveXPool[indx].open( "GET" , req[0] , true );
				ActiveXPool[indx].onreadystatechange = function ()
					{
						if (ActiveXPool[indx].readyState == 4 || ActiveXPool[indx].readyState == 'complete')
						{
							//alert(ActiveXPool[indx].responseText);
							eval(ActiveXPool[indx].responseText);
							//req[1](ActiveXPool[indx].responseText);
						}
					};
			     ActiveXPool[indx].send();
			}
		}
	}

	//var timer = window.setInterval(HandleRequest,750);
