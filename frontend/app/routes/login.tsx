import "./login.css";
import { useNavigate, useSearchParams } from "react-router";
import { useActionState, useEffect } from "react";
import LoginForm from "~/components/login-form";
import { useUserStore } from "~/stores/user-store";

export default function Login() {

    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const rawFrom = searchParams.get("from") || "/new/";
    const from = rawFrom.startsWith("/new") ? rawFrom.slice(4) || "/" : rawFrom;
    const { loginUser } = useUserStore();

    async function handleLogin(
        prevState : {
            success: boolean;
            error: string | null;
        } | null,
        formData : FormData,
    ){
        const email = formData.get("email") as string;
        const password = formData.get("password") as string;
        await loginUser(email, password);
        const { loginError } = useUserStore.getState();
        if (loginError) {
            return { success: false, error: loginError };
        }
        return { success: true, error: null };
    }

    const [state, formAction, isPending] = useActionState(handleLogin, null);

    useEffect(() => {
        if (state?.success) {
            navigate(from, { replace: true });
        }
    }, [state, navigate, from]);

    return (
        <main className="login-page">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-12 col-sm-11 col-md-9 col-lg-8">
                        <section className="login-card" aria-label="Formulario de inicio de sesión">
                            <div className="login-header">
                                <h1 className="login-title">Iniciar sesión</h1>
                            </div>
                            <LoginForm
                                actionState={[state, formAction, isPending]}
                                onCancel={() => navigate("/")}
                            />
                        </section>
                    </div>
                </div>
            </div>
        </main>
    );
}