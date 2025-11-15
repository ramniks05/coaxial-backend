/**
 * Modern Loader Utility
 * Provides reusable loading states and animations across the application
 */
class LoaderManager {
    constructor() {
        this.activeLoaders = new Set();
        this.defaultOptions = {
            type: 'spinner',
            size: 'medium',
            color: '#3498db',
            text: '',
            overlay: false,
            duration: 0
        };
    }

    /**
     * Show a loader
     * @param {string} elementId - ID of the element to show loader in
     * @param {Object} options - Loader options
     */
    show(elementId, options = {}) {
        const element = document.getElementById(elementId);
        if (!element) {
            console.warn(`Element with ID '${elementId}' not found`);
            return;
        }

        const config = { ...this.defaultOptions, ...options };
        const loaderId = `loader-${elementId}-${Date.now()}`;
        
        // Store original content
        element.dataset.originalContent = element.innerHTML;
        element.dataset.loaderId = loaderId;
        
        // Create loader HTML
        const loaderHTML = this.createLoaderHTML(config);
        element.innerHTML = loaderHTML;
        
        // Add loading class
        element.classList.add('loading');
        
        // Store active loader
        this.activeLoaders.add(loaderId);
        
        // Auto-hide if duration is set
        if (config.duration > 0) {
            setTimeout(() => this.hide(elementId), config.duration);
        }
        
        return loaderId;
    }

    /**
     * Hide a loader
     * @param {string} elementId - ID of the element to hide loader from
     */
    hide(elementId) {
        const element = document.getElementById(elementId);
        if (!element) return;

        const loaderId = element.dataset.loaderId;
        if (loaderId) {
            this.activeLoaders.delete(loaderId);
        }

        // Restore original content
        if (element.dataset.originalContent) {
            element.innerHTML = element.dataset.originalContent;
            delete element.dataset.originalContent;
        }

        // Remove loading class
        element.classList.remove('loading');
        delete element.dataset.loaderId;
    }

    /**
     * Show overlay loader
     * @param {Object} options - Loader options
     */
    showOverlay(options = {}) {
        const config = { ...this.defaultOptions, ...options, overlay: true };
        const overlayId = 'overlay-loader';
        
        // Remove existing overlay if any
        this.hideOverlay();
        
        const overlay = document.createElement('div');
        overlay.id = overlayId;
        overlay.className = 'overlay-loader';
        overlay.innerHTML = this.createOverlayHTML(config);
        
        document.body.appendChild(overlay);
        document.body.style.overflow = 'hidden';
        
        return overlayId;
    }

    /**
     * Hide overlay loader
     */
    hideOverlay() {
        const overlay = document.getElementById('overlay-loader');
        if (overlay) {
            overlay.remove();
            document.body.style.overflow = '';
        }
    }

    /**
     * Show button loader
     * @param {string} buttonId - ID of the button
     * @param {Object} options - Loader options
     */
    showButtonLoader(buttonId, options = {}) {
        const button = document.getElementById(buttonId);
        if (!button) return;

        const config = { ...this.defaultOptions, ...options };
        button.classList.add('btn-loader');
        button.disabled = true;
        
        const originalText = button.textContent;
        button.dataset.originalText = originalText;
        
        const loaderHTML = this.createButtonLoaderHTML(config);
        button.innerHTML = loaderHTML;
        
        return buttonId;
    }

    /**
     * Hide button loader
     * @param {string} buttonId - ID of the button
     */
    hideButtonLoader(buttonId) {
        const button = document.getElementById(buttonId);
        if (!button) return;

        button.classList.remove('btn-loader');
        button.disabled = false;
        
        if (button.dataset.originalText) {
            button.textContent = button.dataset.originalText;
            delete button.dataset.originalText;
        }
    }

    /**
     * Show dropdown loader
     * @param {string} selectId - ID of the select element
     */
    showDropdownLoader(selectId) {
        const select = document.getElementById(selectId);
        if (!select) return;

        select.classList.add('dropdown-loader');
        select.disabled = true;
        
        // Add loading option
        const loadingOption = document.createElement('option');
        loadingOption.value = '';
        loadingOption.textContent = 'Loading...';
        loadingOption.disabled = true;
        loadingOption.selected = true;
        
        // Clear existing options except the first one
        const firstOption = select.firstElementChild;
        select.innerHTML = '';
        if (firstOption) {
            select.appendChild(firstOption);
        }
        select.appendChild(loadingOption);
    }

    /**
     * Hide dropdown loader
     * @param {string} selectId - ID of the select element
     */
    hideDropdownLoader(selectId) {
        const select = document.getElementById(selectId);
        if (!select) return;

        select.classList.remove('dropdown-loader');
        select.disabled = false;
    }

    /**
     * Show skeleton loader
     * @param {string} elementId - ID of the element
     * @param {Object} options - Skeleton options
     */
    showSkeleton(elementId, options = {}) {
        const element = document.getElementById(elementId);
        if (!element) return;

        const config = {
            lines: 3,
            avatar: false,
            ...options
        };

        element.dataset.originalContent = element.innerHTML;
        element.innerHTML = this.createSkeletonHTML(config);
        element.classList.add('skeleton-container');
    }

    /**
     * Hide skeleton loader
     * @param {string} elementId - ID of the element
     */
    hideSkeleton(elementId) {
        const element = document.getElementById(elementId);
        if (!element) return;

        if (element.dataset.originalContent) {
            element.innerHTML = element.dataset.originalContent;
            delete element.dataset.originalContent;
        }
        element.classList.remove('skeleton-container');
    }

    /**
     * Create loader HTML based on type
     * @param {Object} config - Loader configuration
     */
    createLoaderHTML(config) {
        const { type, size, color, text } = config;
        
        switch (type) {
            case 'spinner':
                return `<div class="spinner-loader ${size}" style="border-top-color: ${color}"></div>${text ? `<span class="loader-text">${text}</span>` : ''}`;
            
            case 'dots':
                return `<div class="dots-loader" style="--color: ${color}">
                    <div></div><div></div><div></div><div></div>
                </div>${text ? `<span class="loader-text">${text}</span>` : ''}`;
            
            case 'pulse':
                return `<div class="pulse-loader ${size}" style="background: ${color}"></div>${text ? `<span class="loader-text">${text}</span>` : ''}`;
            
            default:
                return `<div class="spinner-loader ${size}" style="border-top-color: ${color}"></div>${text ? `<span class="loader-text">${text}</span>` : ''}`;
        }
    }

    /**
     * Create overlay HTML
     * @param {Object} config - Loader configuration
     */
    createOverlayHTML(config) {
        const { type, size, color, text } = config;
        const loaderHTML = this.createLoaderHTML(config);
        
        return `
            <div class="loader-content">
                ${loaderHTML}
                ${text ? `<div class="loader-text">${text}</div>` : ''}
            </div>
        `;
    }

    /**
     * Create button loader HTML
     * @param {Object} config - Loader configuration
     */
    createButtonLoaderHTML(config) {
        const { type, color } = config;
        
        switch (type) {
            case 'spinner':
                return `<span class="btn-text">${config.text || 'Loading...'}</span><div class="spinner-loader small" style="border-top-color: ${color}"></div>`;
            default:
                return `<span class="btn-text">${config.text || 'Loading...'}</span><div class="spinner-loader small" style="border-top-color: ${color}"></div>`;
        }
    }

    /**
     * Create skeleton HTML
     * @param {Object} config - Skeleton configuration
     */
    createSkeletonHTML(config) {
        const { lines, avatar } = config;
        let html = '';
        
        if (avatar) {
            html += '<div class="skeleton-avatar"></div>';
        }
        
        for (let i = 0; i < lines; i++) {
            const width = i === lines - 1 ? 'short' : i === 0 ? 'long' : 'medium';
            html += `<div class="skeleton-loader skeleton-text ${width}"></div>`;
        }
        
        return html;
    }

    /**
     * Show progress loader
     * @param {string} elementId - ID of the element
     * @param {number} progress - Progress percentage (0-100)
     */
    showProgress(elementId, progress = 0) {
        const element = document.getElementById(elementId);
        if (!element) return;

        element.innerHTML = `
            <div class="progress-loader">
                <div class="progress-bar" style="width: ${progress}%; background: #3498db;"></div>
            </div>
        `;
    }

    /**
     * Hide all loaders
     */
    hideAll() {
        this.activeLoaders.forEach(loaderId => {
            const elements = document.querySelectorAll(`[data-loader-id="${loaderId}"]`);
            elements.forEach(element => {
                this.hide(element.id);
            });
        });
        this.hideOverlay();
    }

    /**
     * Get loading state
     * @param {string} elementId - ID of the element
     */
    isLoading(elementId) {
        const element = document.getElementById(elementId);
        return element && element.classList.contains('loading');
    }
}

// Create global instance
window.loaderManager = new LoaderManager();

// Utility functions for common use cases
window.showLoader = (elementId, options) => window.loaderManager.show(elementId, options);
window.hideLoader = (elementId) => window.loaderManager.hide(elementId);
window.showOverlay = (options) => window.loaderManager.showOverlay(options);
window.hideOverlay = () => window.loaderManager.hideOverlay();
window.showButtonLoader = (buttonId, options) => window.loaderManager.showButtonLoader(buttonId, options);
window.hideButtonLoader = (buttonId) => window.loaderManager.hideButtonLoader(buttonId);
window.showDropdownLoader = (selectId) => window.loaderManager.showDropdownLoader(selectId);
window.hideDropdownLoader = (selectId) => window.loaderManager.hideDropdownLoader(selectId);
window.showSkeleton = (elementId, options) => window.loaderManager.showSkeleton(elementId, options);
window.hideSkeleton = (elementId) => window.loaderManager.hideSkeleton(elementId);

// Enhanced fetch with automatic loading states
window.fetchWithLoader = async (url, options = {}, loaderElementId = null, loaderOptions = {}) => {
    if (loaderElementId) {
        showLoader(loaderElementId, loaderOptions);
    }
    
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } finally {
        if (loaderElementId) {
            hideLoader(loaderElementId);
        }
    }
};

// Enhanced button click with loading state
window.buttonClickWithLoader = async (buttonId, asyncFunction, options = {}) => {
    const button = document.getElementById(buttonId);
    if (!button) return;
    
    showButtonLoader(buttonId, options);
    
    try {
        await asyncFunction();
    } catch (error) {
        console.error('Button action failed:', error);
        // You can add error handling here
    } finally {
        hideButtonLoader(buttonId);
    }
};

