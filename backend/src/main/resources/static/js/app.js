async function loadMore(event, url, list, item) {
    event.preventDefault();

    const button = event.currentTarget;
    const spinner = document.getElementById('loading-spinner');

    button.classList.add('d-none');
    spinner.classList.remove('d-none');

    try {
        const delay = new Promise(resolve => setTimeout(resolve, 400)); // Simulates network delay to show spinner
        const [response] = await Promise.all([fetch(url), delay]);
        if (!response.ok) throw new Error('Network response was not ok');

        const hasMore = response.headers.get('X-Has-More') === 'true';
        const data = await response.text();
        
        const temp = document.createElement('template');
        temp.innerHTML = data.trim(); 
        
        const newItems = temp.content.querySelectorAll("." + item);
        const targetList = document.getElementById(list);

        newItems.forEach(node => {
            targetList.appendChild(document.importNode(node, true));
        });

        const nextUrl = url.replace(/page=(\d+)/, (match, p1) => `page=${parseInt(p1) + 1}`);
        button.setAttribute('onclick', `loadMore(event, '${nextUrl}', '${list}', '${item}')`);

        if (hasMore) {
            button.classList.remove('d-none');
            spinner.classList.add('d-none');
        } else {
            spinner.remove();
            button.remove();
        }

    } catch (error) {
        console.error('Error loading more items:', error);
        button.classList.remove('d-none');
        spinner.classList.add('d-none');
    }
}