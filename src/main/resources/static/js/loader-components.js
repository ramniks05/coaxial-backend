/**
 * Loader Components
 * Reusable loader components for different use cases
 */

// Card Loader Component
class CardLoader {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        this.options = {
            lines: 3,
            avatar: true,
            title: true,
            ...options
        };
    }

    show() {
        if (!this.container) return;
        
        this.container.dataset.originalContent = this.container.innerHTML;
        this.container.innerHTML = this.createCardSkeleton();
        this.container.classList.add('card-loader');
    }

    hide() {
        if (!this.container) return;
        
        if (this.container.dataset.originalContent) {
            this.container.innerHTML = this.container.dataset.originalContent;
            delete this.container.dataset.originalContent;
        }
        this.container.classList.remove('card-loader');
    }

    createCardSkeleton() {
        let html = '<div class="card-loader">';
        
        if (this.options.avatar) {
            html += '<div class="skeleton-avatar"></div>';
        }
        
        if (this.options.title) {
            html += '<div class="skeleton-loader skeleton-text long"></div>';
        }
        
        for (let i = 0; i < this.options.lines; i++) {
            const width = i === this.options.lines - 1 ? 'short' : 'medium';
            html += `<div class="skeleton-loader skeleton-text ${width}"></div>`;
        }
        
        html += '</div>';
        return html;
    }
}

// Table Loader Component
class TableLoader {
    constructor(tableId, options = {}) {
        this.table = document.getElementById(tableId);
        this.options = {
            rows: 5,
            columns: 4,
            ...options
        };
    }

    show() {
        if (!this.table) return;
        
        this.table.dataset.originalContent = this.table.innerHTML;
        this.table.innerHTML = this.createTableSkeleton();
        this.table.classList.add('table-loader');
    }

    hide() {
        if (!this.table) return;
        
        if (this.table.dataset.originalContent) {
            this.table.innerHTML = this.table.dataset.originalContent;
            delete this.table.dataset.originalContent;
        }
        this.table.classList.remove('table-loader');
    }

    createTableSkeleton() {
        let html = '<thead><tr>';
        for (let i = 0; i < this.options.columns; i++) {
            html += '<th><div class="skeleton-loader skeleton-text short"></div></th>';
        }
        html += '</tr></thead><tbody>';
        
        for (let i = 0; i < this.options.rows; i++) {
            html += '<tr>';
            for (let j = 0; j < this.options.columns; j++) {
                html += '<td><div class="skeleton-loader skeleton-text medium"></div></td>';
            }
            html += '</tr>';
        }
        html += '</tbody>';
        
        return html;
    }
}

// List Loader Component
class ListLoader {
    constructor(listId, options = {}) {
        this.list = document.getElementById(listId);
        this.options = {
            items: 5,
            avatar: true,
            ...options
        };
    }

    show() {
        if (!this.list) return;
        
        this.list.dataset.originalContent = this.list.innerHTML;
        this.list.innerHTML = this.createListSkeleton();
        this.list.classList.add('list-loader');
    }

    hide() {
        if (!this.list) return;
        
        if (this.list.dataset.originalContent) {
            this.list.innerHTML = this.list.dataset.originalContent;
            delete this.list.dataset.originalContent;
        }
        this.list.classList.remove('list-loader');
    }

    createListSkeleton() {
        let html = '';
        
        for (let i = 0; i < this.options.items; i++) {
            html += '<div class="list-item-loader">';
            
            if (this.options.avatar) {
                html += '<div class="skeleton-avatar"></div>';
            }
            
            html += '<div class="list-content">';
            html += '<div class="skeleton-loader skeleton-text long"></div>';
            html += '<div class="skeleton-loader skeleton-text short"></div>';
            html += '</div>';
            html += '</div>';
        }
        
        return html;
    }
}

// Chart Loader Component
class ChartLoader {
    constructor(chartId, options = {}) {
        this.chart = document.getElementById(chartId);
        this.options = {
            type: 'bar', // bar, line, pie
            ...options
        };
    }

    show() {
        if (!this.chart) return;
        
        this.chart.dataset.originalContent = this.chart.innerHTML;
        this.chart.innerHTML = this.createChartSkeleton();
        this.chart.classList.add('chart-loader');
    }

    hide() {
        if (!this.chart) return;
        
        if (this.chart.dataset.originalContent) {
            this.chart.innerHTML = this.chart.dataset.originalContent;
            delete this.chart.dataset.originalContent;
        }
        this.chart.classList.remove('chart-loader');
    }

    createChartSkeleton() {
        let html = '<div class="chart-skeleton">';
        
        if (this.options.type === 'bar') {
            html += '<div class="chart-bars">';
            for (let i = 0; i < 5; i++) {
                const height = Math.random() * 60 + 20;
                html += `<div class="chart-bar" style="height: ${height}%"></div>`;
            }
            html += '</div>';
        } else if (this.options.type === 'line') {
            html += '<div class="chart-line"></div>';
        } else if (this.options.type === 'pie') {
            html += '<div class="chart-pie"></div>';
        }
        
        html += '</div>';
        return html;
    }
}

// Form Loader Component
class FormLoader {
    constructor(formId, options = {}) {
        this.form = document.getElementById(formId);
        this.options = {
            fields: 3,
            ...options
        };
    }

    show() {
        if (!this.form) return;
        
        this.form.dataset.originalContent = this.form.innerHTML;
        this.form.innerHTML = this.createFormSkeleton();
        this.form.classList.add('form-loader');
    }

    hide() {
        if (!this.form) return;
        
        if (this.form.dataset.originalContent) {
            this.form.innerHTML = this.form.dataset.originalContent;
            delete this.form.dataset.originalContent;
        }
        this.form.classList.remove('form-loader');
    }

    createFormSkeleton() {
        let html = '';
        
        for (let i = 0; i < this.options.fields; i++) {
            html += '<div class="form-field-skeleton">';
            html += '<div class="skeleton-loader skeleton-text short"></div>';
            html += '<div class="skeleton-loader skeleton-text long"></div>';
            html += '</div>';
        }
        
        html += '<div class="form-actions-skeleton">';
        html += '<div class="skeleton-loader skeleton-text medium"></div>';
        html += '<div class="skeleton-loader skeleton-text medium"></div>';
        html += '</div>';
        
        return html;
    }
}

// Progress Loader Component
class ProgressLoader {
    constructor(containerId, options = {}) {
        this.container = document.getElementById(containerId);
        this.options = {
            showPercentage: true,
            animated: true,
            ...options
        };
        this.progress = 0;
    }

    show() {
        if (!this.container) return;
        
        this.container.dataset.originalContent = this.container.innerHTML;
        this.container.innerHTML = this.createProgressHTML();
        this.container.classList.add('progress-loader-container');
    }

    hide() {
        if (!this.container) return;
        
        if (this.container.dataset.originalContent) {
            this.container.innerHTML = this.container.dataset.originalContent;
            delete this.container.dataset.originalContent;
        }
        this.container.classList.remove('progress-loader-container');
    }

    updateProgress(percentage) {
        this.progress = Math.min(100, Math.max(0, percentage));
        const progressBar = this.container.querySelector('.progress-bar');
        const percentageText = this.container.querySelector('.progress-percentage');
        
        if (progressBar) {
            progressBar.style.width = `${this.progress}%`;
        }
        
        if (percentageText && this.options.showPercentage) {
            percentageText.textContent = `${Math.round(this.progress)}%`;
        }
    }

    createProgressHTML() {
        return `
            <div class="progress-loader">
                <div class="progress-bar" style="width: 0%"></div>
            </div>
            ${this.options.showPercentage ? '<div class="progress-percentage">0%</div>' : ''}
        `;
    }
}

// Global loader components manager
class LoaderComponentsManager {
    constructor() {
        this.components = new Map();
    }

    createCardLoader(containerId, options) {
        const loader = new CardLoader(containerId, options);
        this.components.set(containerId, loader);
        return loader;
    }

    createTableLoader(tableId, options) {
        const loader = new TableLoader(tableId, options);
        this.components.set(tableId, loader);
        return loader;
    }

    createListLoader(listId, options) {
        const loader = new ListLoader(listId, options);
        this.components.set(listId, loader);
        return loader;
    }

    createChartLoader(chartId, options) {
        const loader = new ChartLoader(chartId, options);
        this.components.set(chartId, loader);
        return loader;
    }

    createFormLoader(formId, options) {
        const loader = new FormLoader(formId, options);
        this.components.set(formId, loader);
        return loader;
    }

    createProgressLoader(containerId, options) {
        const loader = new ProgressLoader(containerId, options);
        this.components.set(containerId, loader);
        return loader;
    }

    getLoader(componentId) {
        return this.components.get(componentId);
    }

    hideAll() {
        this.components.forEach(loader => {
            if (loader.hide) loader.hide();
        });
    }
}

// Create global instance
window.loaderComponents = new LoaderComponentsManager();

// Utility functions
window.createCardLoader = (containerId, options) => window.loaderComponents.createCardLoader(containerId, options);
window.createTableLoader = (tableId, options) => window.loaderComponents.createTableLoader(tableId, options);
window.createListLoader = (listId, options) => window.loaderComponents.createListLoader(listId, options);
window.createChartLoader = (chartId, options) => window.loaderComponents.createChartLoader(chartId, options);
window.createFormLoader = (formId, options) => window.loaderComponents.createFormLoader(formId, options);
window.createProgressLoader = (containerId, options) => window.loaderComponents.createProgressLoader(containerId, options);

