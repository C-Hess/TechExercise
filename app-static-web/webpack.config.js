const path = require('path');
//const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
//const BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = {
    mode: 'production',
    entry: './build/index.js',
    output: {
        filename: './js/index.bundle.js',
        path: path.resolve(__dirname, 'dist')
    }
//    plugins: [
//        new BundleAnalyzerPlugin()
//    ],
//    optimization: {
//        minimizer: [new UglifyJsPlugin()],
//    },
}
