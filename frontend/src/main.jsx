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
            background: '#ffffff',
            color: '#242a25',
            border: '1px solid #e4e8e2',
          },
          success: {
            iconTheme: {
              primary: '#39734b',
              secondary: '#ffffff',
            },
          },
          error: {
            iconTheme: {
              primary: '#ad5144',
              secondary: '#ffffff',
            },
          },
        }}
      />
    </BrowserRouter>
  </React.StrictMode>,
);
