import { PathLike } from 'node:fs';
import path from 'node:path';
import which from "which";
import EventEmitter from "eventemitter3";
import execa from "execa";

export type MergerOptions = {
	javaBinary: string;
	javaOptions: string;
	cli: PathLike;
}

const defaultOptions: MergerOptions = {
	javaBinary: "java",
	javaOptions: "-server -Xms1G -Xmx1G",
	cli: path.join(__dirname, "cli.jar")
};

export type pageStamp = 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight';

type Result = { code: number, stdout: string, stderr: string };

export class Merger extends EventEmitter {

	private readonly options: MergerOptions;
	private proc?: execa.ExecaChildProcess | null;

	constructor(options: Partial<MergerOptions> = {}) {
		super();
		this.options = Object.assign({}, defaultOptions, options)
	}
	async exec(pdfs: PathLike[], output: PathLike, pageStamp?: pageStamp): Promise<Result> {
		/*  resolve path to Java binary  */
		const javaBinary = await which(this.options.javaBinary).catch(() => {
			throw new Error("unable to find mandatory Java binary " +
				`"${this.options.javaBinary}" in your $PATH`)
		});
		const result = { code: -1, stdout: "", stderr: "" };
		const args = ["-f", ...pdfs as string[], "-o", output as string];
		if (pageStamp) {
			args.push("-p", pageStamp);
		}
		return new Promise((resolve, reject) => {
			/*  spawn the process  */
			this.emit("start");
			this.emit("debug", "cli: starting");

			this.proc = execa(javaBinary, [
				...this.options.javaOptions.split(/\s+/),
				"-jar", `${this.options.cli}`,
				...args
			], { stdio: ["pipe", "pipe", "pipe"] });

			/*  detect shutdown of process  */
			this.proc.on("close", (code: number) => {
				this.emit("close", code);
				this.emit("debug", `cli: close (code: ${code})`);
				this.proc = null;
				result.code = code;
				resolve(result);
			});

			/*  receive data on stdout from process  */
			this.proc.stdout?.on("data", (chunk: any) => {
				let data = chunk.toString();
				data = data.replace(/\r?\n/g, "\n");
				this.emit("stdout", data);
				this.emit("debug", `cli: stdout: "${data}"`);
				result.stdout += data;
			});

			/*  receive data on stderr from process  */
			this.proc.stderr?.on("data", (chunk: any) => {
				let data = chunk.toString();
				data = data.replace(/\r?\n/g, "\n");
				this.emit("stderr", data);
				this.emit("debug", `cli: stderr: "${data}"`);
				result.stderr += data;
			});
		})
	}
}
