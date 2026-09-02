import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import App from './App.jsx';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
      <Toaster 
        position="top-right"
        toastOptions={{
          style: {
            background: '#161b22',
            color: '#e6edf3',
            border: '1px solid #30363d',
          },
          success: {
            iconTheme: {
              primary: '#2ea043',
              secondary: '#161b22',
            },
          },
          error: {
            iconTheme: {
              primary: '#f85149',
              secondary: '#161b22',
            },
          },
        }}
      />
    </BrowserRouter>
  </React.StrictMode>,
);
