export enum Environment {
    LOCAL = 'local',
    STAGING = 'staging',
    PRODUCTION = 'production',
}

export const EnvironmentConfig: Record<Environment, { baseURL: string }> = {
    [Environment.LOCAL]: {
        baseURL: 'http://localhost:8080',
    },
    [Environment.STAGING]: {
        baseURL: 'https://staging.pma.com',
    },
    [Environment.PRODUCTION]: {
        baseURL: 'https://pma.com',
    },
};

export function getEnvironment(): Environment {
    const env = process.env.ENVIRONMENT as Environment | undefined;

    if (!env) {
        return Environment.LOCAL;
    }

    if (!Object.values(Environment).includes(env)) {
        throw new Error(`Invalid ENVIRONMENT: ${env}`);
    }

    return env;
}