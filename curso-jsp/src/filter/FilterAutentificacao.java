package filter;

import java.io.IOException;

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

@WebFilter(urlPatterns = { "/principal/*" }) /* Intercepta todas as requisições que vierem do projeto ou mapeamento */
public class FilterAutentificacao implements Filter {

	public FilterAutentificacao() {

	}

	/* Encerra os processos quando o servidor for parado */
	/* Mataria o processo de conexão com o banco de dados */
	public void destroy() {

	}

	/*
	 * Intercepta as requisições e as respostas no sistema Tudo no sistema passa por
	 * aqui Ex: Validação de autentificação Dar commit e rolback de transações no
	 * banco Validar e fazer redirecionamento de páginas
	 */

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		HttpSession session = req.getSession();

		String usuarioLogado = (String) session.getAttribute("usuario");

		String urlParaAutenticar = req.getServletPath(); // URL que está sendo acessada

		// Validar se está logado, se não redireciona para a tela de login

		if (usuarioLogado == null
				&& !urlParaAutenticar.equalsIgnoreCase("/principal/ServletLogin")) {/* Não está logado */

			RequestDispatcher redireciona = request.getRequestDispatcher("/index.jsp?url=" + urlParaAutenticar);

			request.setAttribute("msg", "Por favor realize o login");

			redireciona.forward(request, response);

			return; // Para a execução e redireciona para o login

		} else {
			chain.doFilter(request, response);
		}

	}

	/* Inicia os processos ou recursos quando o servidor sobe o projeto */
	// Inicia conexão com o banco de dados
	public void init(FilterConfig fConfig) throws ServletException {

	}

}
