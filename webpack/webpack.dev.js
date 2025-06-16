const webpackMerge = require('webpack-merge').merge;
const BrowserSyncPlugin = require('browser-sync-webpack-plugin');
const SimpleProgressWebpackPlugin = require('simple-progress-webpack-plugin');
const WebpackNotifierPlugin = require('webpack-notifier');
const path = require('path');
const sass = require('sass');
const postcssRTLCSS = require('postcss-rtlcss');
const fs = require('fs'); // ADD THIS LINE

const utils = require('./utils.js');
const commonConfig = require('./webpack.common.js');

const ENV = 'development';

module.exports = async options =>
  webpackMerge(await commonConfig({ env: ENV }), {
    devtool: 'cheap-module-source-map',
    mode: ENV,
    entry: ['./src/main/webapp/app/index'],
    output: {
      path: utils.root('build/resources/main/static/'),
      filename: '[name].[contenthash:8].js',
      chunkFilename: '[name].[chunkhash:8].chunk.js',
    },
    optimization: {
      moduleIds: 'named',
    },
    module: {
      rules: [
        {
          test: /\.(sa|sc|c)ss$/,
          use: [
            'style-loader',
            {
              loader: 'css-loader',
              options: { url: false },
            },
            {
              loader: 'postcss-loader',
              options: {
                postcssOptions: {
                  plugins: [postcssRTLCSS()],
                },
              },
            },
            {
              loader: 'sass-loader',
              options: { implementation: sass },
            },
          ],
        },
      ],
    },
    devServer: {
      hot: true,
      static: {
        directory: './build/resources/main/static/',
      },
      port: 9060,
      allowedHosts: ['desktop-avlojj8.tail2f1f06.ts.net', 'localhost'],
      // CHANGED: HTTPS now goes under 'server' property
      server: {
        type: 'https',
        options: {
          key: fs.readFileSync('C:/Users/PC/Documents/pfe/capstone/https/desktop-avlojj8.tail2f1f06.ts.net.key'),
          cert: fs.readFileSync('C:/Users/PC/Documents/pfe/capstone/https/desktop-avlojj8.tail2f1f06.ts.net.crt'),
        },
      },
      proxy: [
        {
          context: ['/api', '/services', '/management', '/v3/api-docs', '/h2-console', '/auth', '/oauth2', '/login'],
          target: 'https://desktop-avlojj8.tail2f1f06.ts.net:8080', // Using Tailscale hostname that matches certificate
          secure: false,
          changeOrigin: true,
        },
      ],
      historyApiFallback: true,
    },
    stats: process.env.JHI_DISABLE_WEBPACK_LOGS ? 'none' : options.stats,
    plugins: [
      process.env.JHI_DISABLE_WEBPACK_LOGS
        ? null
        : new SimpleProgressWebpackPlugin({
            format: options.stats === 'minimal' ? 'compact' : 'expanded',
          }),
      new BrowserSyncPlugin(
        {
          https: true,
          host: 'desktop-avlojj8.tail2f1f06.ts.net',
          port: 9000,
          proxy: {
            target: 'https://desktop-avlojj8.tail2f1f06.ts.net:9060',
            ws: true,
            proxyOptions: {
              changeOrigin: false,
            },
          },
          socket: {
            clients: {
              heartbeatTimeout: 60000,
            },
          },
        },
        {
          reload: false,
        },
      ),
      new WebpackNotifierPlugin({
        title: 'Allomed',
        contentImage: path.join(__dirname, 'logo-jhipster.png'),
      }),
    ].filter(Boolean),
  });
