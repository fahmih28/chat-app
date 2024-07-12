import Container from "@mui/material/Container";
import style from './login.module.css';
import {Button, TextField} from "@mui/material";

function LoginPage() {

    return <div className={style.container}>
        <Container className={style.lgc} maxWidth="sm">
            <form>
                <h3>Login</h3>
                <TextField variant="standard" label={"Email"} margin="normal" required inputProps={{size: 75}}/>
                <TextField variant="standard" type="password" margin="normal" label={"Password"} required inputProps={{size: 75}}/>
                <div className={style.divButton}>
                    <Button type="button" color="primary" variant="outlined">Submit</Button>
                </div>
            </form>
        </Container>
    </div>
}

export default LoginPage;