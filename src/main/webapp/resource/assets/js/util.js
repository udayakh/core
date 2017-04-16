	function removeA(arr) {
							    var what, a = arguments, L = a.length, ax;
							    while (L > 1 && arr.length) {
							        what = a[--L];
							        while ((ax= arr.indexOf(what)) !== -1) {
							            arr.splice(ax, 1);
							        }
							    }
							    return arr;
							}
	
	function ArrNoDupe(a) {
	    var temp = {};
	    for (var i = 0; i < a.length; i++)
	        temp[a[i]] = true;
	    var r = [];
	    for (var k in temp)
	        r.push(k);
	    return r;
	}