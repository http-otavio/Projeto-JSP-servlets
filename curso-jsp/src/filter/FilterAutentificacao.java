package filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import connection.SingleConnectionBanco;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/principal/*" }) /* Intercepta todas as requisi��es que vierem do projeto ou mapeamento */
public class FilterAutentificacao implements Filter {
	
	
	
	private static Connection connection;
	

	public FilterAutentificacao() {

	}

	/* Encerra os processos quando o servidor for parado */
	/* Mataria o processo de conex�o com o banco de dados */
	public void destroy() {
        try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Intercepta as requisi��es e as respostas no sistema Tudo no sistema passa por
	 * aqui Ex: Valida��o de autentifica��o Dar commit e rolback de transa��es no
	 * banco Validar e fazer redirecionamento de p�ginas
	 */

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
        try {
			HttpServletRequest req = (HttpServletRequest) request;
	
			HttpSession session = req.getSession();
	
			String usuarioLogado = (String) session.getAttribute("usuario");
	
			String urlParaAutenticar = req.getServletPath(); // URL que est� sendo acessada
	
			// Validar se est� logado, se n�o redireciona para a tela de login
	
			if (usuarioLogado == null
					&& !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) {/* N�o est� logado */
	
				RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" + urlParaAutenticar);
	
				request.setAttribute("msg", "Por favor realize o login");
	
				redireciona.forward(request, response);
	
				return; // Para a execu��o e redireciona para o login
	
			} else {
				chain.doFilter(request, response);
			}
        
			connection.commit();//Se deu tudo certo commita as altera��es no banco de dados
			
        }catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				
				e1.printStackTrace();
			}
		}
	}

	/* Inicia os processos ou recursos quando o servidor sobe o projeto */
	// Inicia conex�o com o banco de dados
	public void init(FilterConfig fConfig) throws ServletException {
        connection = SingleConnectionBanco.getConnection();
	}

}
