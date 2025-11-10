import React from 'react';
import { Route, Routes } from 'react-router-dom';

import { DefaultLayout } from './components/layouts/DefaultLayout';
import { ProtectedLayout } from './components/layouts/ProtectedLayout';

import { CreateDeclarationPage } from './pages/CreateDeclarationPage';
import { DashboardPage } from './pages/DashboardPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { SubmitDeclarationPage } from './pages/SubmitDeclarationPage';

function App(): React.JSX.Element {
  return (
    <Routes>
      <Route path="/" element={<DefaultLayout />}>
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/login" element={<LoginPage />} />
      </Route>

      <Route path="/dashboard" element={<ProtectedLayout />}>
        <Route path="/dashboard" element={<DashboardPage />} />
      </Route>

      <Route path="/declaracoes" element={<ProtectedLayout />}>
        <Route path="/declaracoes/criar" element={<CreateDeclarationPage />} />
        <Route path="/declaracoes/enviar" element={<SubmitDeclarationPage />} />
      </Route>
    </Routes>
  );
}

export default App;