var pager = new Pager('results', 25); 
pager.init(); 
pager.showPageNav('pager', 'pageNavPosition'); 
pager.showPage(1);

document.getElementById("resultsperpage").addEventListener('change', function() {
	pager.currentPage = 1;
	pager.showRecords(0, 0);

	pager = new Pager('results', this.value);

	pager.init();
	pager.showPageNav('pager', 'pageNavPosition'); 
	pager.showPage(1);
});