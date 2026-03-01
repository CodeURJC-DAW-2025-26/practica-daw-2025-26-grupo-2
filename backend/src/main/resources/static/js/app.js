async function loadMore(event, url, list, item) {
    event.preventDefault();

    const button = event.currentTarget;
    button.disabled = true;
    button.innerHTML = 'Cargando... <i class="bi bi-arrow-repeat"></i>';

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        const hasMore = response.headers.get('X-Has-More') === 'true';
        const data = await response.text();
        const temp = document.createElement('template');
        temp.innerHTML = data.trim(); 
        
        const newItems = temp.content.querySelectorAll("." + item);
        const targetList = document.getElementById(list);

        if (newItems.length === 0) {
            console.warn(`No se encontraron elementos con la clase: .${item}`);
        }

        newItems.forEach(node => {
            const importedNode = document.importNode(node, true);
            targetList.appendChild(importedNode);
        });
        const nextUrl = url.replace(/page=(\d+)/, (match, p1) => `page=${parseInt(p1) + 1}`);

        if (hasMore) {
            button.disabled = false;
            button.innerHTML = 'Cargar más <i class="bi bi-arrow-down-circle"></i>';
            button.setAttribute('onclick', `loadMore(event, '${nextUrl}', '${list}', '${item}')`);
        } else {
            button.remove();
        }
    } catch (error) {
        console.error('Error loading more items:', error);
        button.disabled = false;
        button.innerHTML = 'Cargar más <i class="bi bi-arrow-down-circle"></i>';
    }
}