import React, { createContext, useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { login as apiLogin, register as apiRegister } from '../api/auth';
import { getMe } from '../api/users';
import client from '../api/client';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      if (token) {
        try {
          const storedUser = localStorage.getItem('mockUser');
          if (storedUser) {
            setUser(JSON.parse(storedUser));
          } else {
            logout();
          }
        } catch (error) {
          console.error("Session expired or invalid token", error);
          logout();
        }
      }
      setLoading(false);
    };

    initAuth();
  }, [token]);

  const login = async (email, password) => {
    try {
      const isMockAdmin = email.includes('admin');
      const mockUser = {
        id: isMockAdmin ? 'admin-1' : 'user-1',
        name: isMockAdmin ? 'Admin Mock' : 'User Mock',
        email: email,
        role: isMockAdmin ? 'ADMIN' : 'USER'
      };
      
      const mockToken = isMockAdmin ? 'mock-admin-token' : 'mock-user-token';
      
      localStorage.setItem('token', mockToken);
      localStorage.setItem('mockUser', JSON.stringify(mockUser));
      setToken(mockToken);
      setUser(mockUser);
      toast.success('Successfully logged in as ' + mockUser.role + ' (Mock)');
      return { success: true };
    } catch (error) {
      toast.error('Mock login failed');
      return { success: false, error: 'Mock error' };
    }
  };

  const register = async (userData) => {
    try {
      const isMockAdmin = userData.email.includes('admin');
      const mockUser = {
        id: isMockAdmin ? 'admin-1' : 'user-1',
        name: userData.name || (isMockAdmin ? 'Admin Mock' : 'User Mock'),
        email: userData.email,
        role: isMockAdmin ? 'ADMIN' : 'USER'
      };
      
      const mockToken = isMockAdmin ? 'mock-admin-token' : 'mock-user-token';
      
      localStorage.setItem('token', mockToken);
      localStorage.setItem('mockUser', JSON.stringify(mockUser));
      setToken(mockToken);
      setUser(mockUser);
      toast.success('Registration successful as ' + mockUser.role + ' (Mock)!');
      return { success: true };
    } catch (error) {
      toast.error('Mock registration failed');
      return { success: false, error: 'Mock error' };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
    delete client.defaults.headers.common['Authorization'];
  };

  const value = {
    user,
    token,
    loading,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
    login,
    register,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
