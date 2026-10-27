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
          const profile = await getMe();
          setUser(profile);
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
      const response = await apiLogin(email, password);
      localStorage.setItem('token', response.accessToken);
      setToken(response.accessToken);
      setUser({
        id: response.userId,
        email: response.email,
        fullName: response.fullName,
        role: response.role,
      });
      toast.success('Successfully logged in');
      return { success: true };
    } catch (error) {
      const message = error.response?.data?.message || 'Login failed';
      toast.error(message);
      return { success: false, error: message };
    }
  };

  const register = async (userData) => {
    try {
      const response = await apiRegister({
        fullName: userData.name,
        email: userData.email,
        password: userData.password,
        college: userData.college,
        department: userData.department,
        yearOfStudy: userData.yearOfStudy ? Number(userData.yearOfStudy) : null,
        skills: userData.skills,
        preferredLanguage: userData.preferredLanguage,
      });
      localStorage.setItem('token', response.accessToken);
      setToken(response.accessToken);
      setUser({
        id: response.userId,
        email: response.email,
        fullName: response.fullName,
        role: response.role,
      });
      toast.success('Registration successful');
      return { success: true };
    } catch (error) {
      const message = error.response?.data?.message || 'Registration failed';
      toast.error(message);
      return { success: false, error: message };
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('mockUser');
    setToken(null);
    setUser(null);
    delete client.defaults.headers.common['Authorization'];
  };

  const value = {
    user,
    token,
    loading,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN' || user?.role === 'ROLE_ADMIN',
    login,
    register,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
