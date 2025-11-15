# Modern Loader System Documentation

## Overview

The Coaxial LMS now includes a comprehensive, modern loader system that provides smooth loading states across the entire application. The system includes multiple loader types, animations, and reusable components.

## Features

### ✅ Multiple Loader Types
- **Spinner Loader** - Classic rotating spinner
- **Dots Loader** - Animated dots sequence
- **Pulse Loader** - Pulsing animation
- **Skeleton Loader** - Content placeholder animation
- **Progress Loader** - Progress bar with percentage
- **Overlay Loader** - Full-screen loading overlay

### ✅ Component-Specific Loaders
- **Card Loader** - For card components
- **Table Loader** - For data tables
- **List Loader** - For list items
- **Chart Loader** - For charts and graphs
- **Form Loader** - For form components

### ✅ Advanced Features
- **Responsive Design** - Works on all screen sizes
- **Dark Mode Support** - Automatic dark mode detection
- **Accessibility** - Screen reader friendly
- **Animation Control** - Smooth transitions
- **Error Handling** - Graceful error states

## Usage Examples

### Basic Loader Usage

```javascript
// Show a spinner loader
showLoader('elementId', {
    type: 'spinner',
    size: 'medium',
    color: '#3498db',
    text: 'Loading...'
});

// Hide the loader
hideLoader('elementId');
```

### Dropdown Loader

```javascript
// Show dropdown loader
showDropdownLoader('selectId');

// Hide dropdown loader
hideDropdownLoader('selectId');
```

### Button Loader

```javascript
// Show button loader
showButtonLoader('buttonId', {
    type: 'spinner',
    text: 'Saving...'
});

// Hide button loader
hideButtonLoader('buttonId');
```

### Overlay Loader

```javascript
// Show overlay loader
showOverlay({
    type: 'dots',
    text: 'Processing request...'
});

// Hide overlay loader
hideOverlay();
```

### Enhanced Fetch with Loader

```javascript
// Automatic loader management
fetchWithLoader('/api/data', {}, 'elementId', {
    type: 'spinner',
    text: 'Loading data...'
})
.then(data => {
    // Handle data
})
.catch(error => {
    // Handle error
});
```

### Button Click with Loader

```javascript
// Automatic button loader
buttonClickWithLoader('submitBtn', async () => {
    await submitForm();
}, {
    type: 'spinner',
    text: 'Submitting...'
});
```

## Component Loaders

### Card Loader

```javascript
// Create card loader
const cardLoader = createCardLoader('cardContainer', {
    lines: 3,
    avatar: true,
    title: true
});

// Show/hide
cardLoader.show();
cardLoader.hide();
```

### Table Loader

```javascript
// Create table loader
const tableLoader = createTableLoader('dataTable', {
    rows: 5,
    columns: 4
});

// Show/hide
tableLoader.show();
tableLoader.hide();
```

### List Loader

```javascript
// Create list loader
const listLoader = createListLoader('itemList', {
    items: 5,
    avatar: true
});

// Show/hide
listLoader.show();
listLoader.hide();
```

### Chart Loader

```javascript
// Create chart loader
const chartLoader = createChartLoader('chartContainer', {
    type: 'bar' // bar, line, pie
});

// Show/hide
chartLoader.show();
chartLoader.hide();
```

### Form Loader

```javascript
// Create form loader
const formLoader = createFormLoader('formContainer', {
    fields: 3
});

// Show/hide
formLoader.show();
formLoader.hide();
```

### Progress Loader

```javascript
// Create progress loader
const progressLoader = createProgressLoader('progressContainer', {
    showPercentage: true,
    animated: true
});

// Show and update progress
progressLoader.show();
progressLoader.updateProgress(50); // 50%
progressLoader.updateProgress(100); // 100%
progressLoader.hide();
```

## CSS Classes

### Loader Types
- `.spinner-loader` - Spinner animation
- `.dots-loader` - Dots animation
- `.pulse-loader` - Pulse animation
- `.skeleton-loader` - Skeleton animation
- `.overlay-loader` - Overlay container

### Sizes
- `.small` - Small size
- `.medium` - Medium size (default)
- `.large` - Large size

### Component Classes
- `.card-loader` - Card loader container
- `.table-loader` - Table loader container
- `.list-loader` - List loader container
- `.chart-loader` - Chart loader container
- `.form-loader` - Form loader container

## Configuration Options

### Loader Options
```javascript
{
    type: 'spinner',        // spinner, dots, pulse, skeleton
    size: 'medium',          // small, medium, large
    color: '#3498db',        // Loader color
    text: 'Loading...',      // Loading text
    overlay: false,          // Show as overlay
    duration: 0              // Auto-hide duration (0 = manual)
}
```

### Component Options
```javascript
// Card Loader
{
    lines: 3,               // Number of skeleton lines
    avatar: true,           // Show avatar skeleton
    title: true             // Show title skeleton
}

// Table Loader
{
    rows: 5,                // Number of skeleton rows
    columns: 4              // Number of skeleton columns
}

// List Loader
{
    items: 5,               // Number of skeleton items
    avatar: true            // Show avatar skeleton
}

// Chart Loader
{
    type: 'bar'             // bar, line, pie
}

// Form Loader
{
    fields: 3               // Number of skeleton fields
}

// Progress Loader
{
    showPercentage: true,   // Show percentage text
    animated: true          // Animate progress bar
}
```

## Integration Examples

### Dashboard Integration

The dashboard now uses the improved loader system:

```javascript
// Course loading with dropdown loader
function loadCourses() {
    showDropdownLoader('courseId');
    
    fetchWithLoader(`/api/courses`, {}, 'courseId', {
        type: 'dots',
        text: 'Loading courses...'
    })
    .then(courses => {
        // Populate dropdown
    })
    .catch(error => {
        console.error('Error:', error);
    });
}
```

### Form Submission

```javascript
// Form submission with button loader
document.getElementById('form').addEventListener('submit', function(e) {
    e.preventDefault();
    
    showButtonLoader('submitBtn', {
        type: 'spinner',
        text: 'Submitting...'
    });
    
    fetch('/api/submit', {
        method: 'POST',
        body: new FormData(this)
    })
    .then(response => response.json())
    .then(data => {
        // Handle success
    })
    .catch(error => {
        // Handle error
    })
    .finally(() => {
        hideButtonLoader('submitBtn');
    });
});
```

## Best Practices

### 1. Use Appropriate Loader Types
- **Spinner**: General loading states
- **Dots**: Data fetching
- **Pulse**: User actions
- **Skeleton**: Content loading
- **Progress**: Long operations

### 2. Provide Loading Text
Always include descriptive loading text:
```javascript
showLoader('elementId', {
    text: 'Loading user data...'
});
```

### 3. Handle Errors Gracefully
```javascript
fetchWithLoader('/api/data', {}, 'elementId')
.then(data => {
    // Handle success
})
.catch(error => {
    hideLoader('elementId');
    showError('Failed to load data');
});
```

### 4. Use Component Loaders for Complex UI
For complex components like tables, cards, and forms, use the specialized component loaders instead of generic loaders.

### 5. Consider User Experience
- Show loaders immediately for user actions
- Use skeleton loaders for content that takes time to load
- Provide progress feedback for long operations

## Browser Support

- **Modern Browsers**: Full support
- **IE11+**: Basic support (fallback animations)
- **Mobile**: Optimized for touch devices
- **Screen Readers**: Full accessibility support

## Performance

- **Lightweight**: Minimal CSS and JavaScript
- **Efficient**: No memory leaks
- **Smooth**: 60fps animations
- **Responsive**: Adapts to device capabilities

## Customization

### Custom Colors
```css
.spinner-loader {
    border-top-color: #your-color;
}

.pulse-loader {
    background: #your-color;
}
```

### Custom Animations
```css
@keyframes custom-spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
}

.custom-loader {
    animation: custom-spin 1s linear infinite;
}
```

## Migration Guide

### From Old System
Replace old loading states:
```javascript
// Old way
let isLoading = false;
if (isLoading) return;
isLoading = true;
// ... fetch logic
isLoading = false;

// New way
showLoader('elementId');
fetchWithLoader('/api/data', {}, 'elementId')
.then(data => {
    // Handle data
});
```

### Updating Existing Code
1. Include the loader CSS and JS files
2. Replace manual loading states with loader functions
3. Use component loaders for complex UI elements
4. Test across different devices and browsers

## Troubleshooting

### Common Issues

1. **Loader not showing**: Check element ID exists
2. **Animation not smooth**: Check CSS is loaded
3. **Memory leaks**: Always hide loaders when done
4. **Accessibility issues**: Use proper ARIA attributes

### Debug Mode
```javascript
// Enable debug logging
window.loaderManager.debug = true;
```

## Future Enhancements

- [ ] WebSocket loading states
- [ ] Real-time progress updates
- [ ] Custom animation builder
- [ ] Theme customization
- [ ] Performance monitoring

---

For more examples and advanced usage, see the `/static/js/loader.js` and `/static/js/loader-components.js` files.

